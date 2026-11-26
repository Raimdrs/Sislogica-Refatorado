# Funcionalidades do Microsserviço Go / gRPC (`services/parser`) — Sílógica

Guia prático das funcionalidades expostas pelo microsserviço de lógica proposicional em **Go + gRPC** (`sislogica.parser.v1.LogicParserService`), explicando **como funcionam**, **como subir o serviço** e **como acessá-las** via `grpcurl`.

> **Contrato oficial:** [`protos/sislogica/parser/v1/parser.proto`](../protos/sislogica/parser/v1/parser.proto) (validado por `buf lint` com regras `STANDARD` e `COMMENTS`).  
> **Decisão arquitetural (ADR):** [`docs/decisoes/001-servico-go.md`](decisoes/001-servico-go.md).

---

## 1. Como Subir o Microsserviço

```bash
cd services/parser
go run main.go
```

- Porta padrão: **`50051`** (configurável via variável de ambiente `GRPC_PORT`, ex.: `GRPC_PORT=50052 go run main.go`).
- Encerramento seguro: responde a `Ctrl+C` (`SIGINT`) ou `SIGTERM` com `GracefulStop` (finaliza requisições em andamento e marca o Health Check como `NOT_SERVING` antes de encerrar).
- O serviço é **100% stateless** (não acessa nem compartilha tabelas do PostgreSQL da API).

---

## 2. Descoberta (gRPC Reflection) e Saúde (gRPC Health Check)

O servidor registra nativamente **gRPC Reflection** (para inspeção dinâmica com `grpcurl`) e o protocolo padrão **`grpc.health.v1.Health`**.

### Listar serviços disponíveis
```bash
grpcurl -plaintext localhost:50051 list
```
Saída esperada:
```text
grpc.health.v1.Health
grpc.reflection.v1.ServerReflection
grpc.reflection.v1alpha.ServerReflection
sislogica.parser.v1.LogicParserService
```

### Verificar saúde do serviço
```bash
grpcurl -plaintext -d '{"service":"sislogica.parser.v1.LogicParserService"}' \
  localhost:50051 grpc.health.v1.Health/Check
```
Resposta esperada:
```json
{
  "status": "SERVING"
}
```

---

## 3. Análise Sintática Formal e Geração de AST (`ParseExpression`)

### Como funciona
1. Recebe uma fórmula proposicional em texto (`expression`) respeitando cancelamento/prazo via `context.Context`.
2. Executa análise léxica (*tokenizer*) e análise sintática (*recursive-descent parser*) respeitando a precedência formal dos conectivos lógicos.
3. Extrai todas as variáveis proposicionais distintas em ordem alfabética e serializa a **Árvore Sintática Abstrata (AST)** em JSON (`ast_json`).

### Operadores aceitos (da maior para a menor precedência)
- **Variáveis:** letras maiúsculas opcionalmente seguidas de dígitos (`P`, `Q`, `R`, `A`, `B`, `P1`).
- **Parênteses:** `( )`
- **Negação (NOT):** `~`, `!`, `¬`
- **Conjunção (AND):** `^`, `&`, `&&`, `∧`
- **Disjunção (OR):** `v` *(minúsculo)*, `|`, `||`, `∨`
- **Implicação (IMPLIES, associativa à direita):** `->`, `→`
- **Bicondicional (IFF):** `<->`, `↔`

### Como acessar
```bash
grpcurl -plaintext -d '{"expression":"(P & Q) -> R"}' \
  localhost:50051 sislogica.parser.v1.LogicParserService/ParseExpression
```

Resposta esperada:
```json
{
  "valid": true,
  "variables": ["P", "Q", "R"],
  "astJson": "{\"kind\":\"BINARY\",\"operator\":\"IMPLIES\",\"left\":{\"kind\":\"BINARY\",\"operator\":\"AND\",\"left\":{\"kind\":\"VARIABLE\",\"value\":\"P\"},\"right\":{\"kind\":\"VARIABLE\",\"value\":\"Q\"}},\"right\":{\"kind\":\"VARIABLE\",\"value\":\"R\"}}"
}
```

---

## 4. Validador de Equivalências Lógicas (`ValidateEquivalence`)

### Como funciona
1. Faz o parsing de `formula_a` e `formula_b` em duas ASTs.
2. Unifica e ordena alfabeticamente o conjunto de variáveis proposicionais presentes em ambas as fórmulas (com limite seguro de até **12 variáveis distintas**, ou $2^{12} = 4096$ linhas).
3. Percorre todas as $2^n$ combinações de valores-verdade (verificando `ctx.Done()` em cada iteração para interromper imediatamente se o prazo gRPC expirar).
4. Retorna `equivalent: true` quando todos os resultados coincidem, ou `equivalent: false` junto com o **primeiro contraexemplo** (`counterexample`) e a tabela-verdade comparativa (`truth_table`).

### Exemplo 1 — Fórmulas equivalentes (Lei de De Morgan: `~(P ^ Q)` ≡ `~P v ~Q`)
```bash
grpcurl -plaintext -d '{"formula_a":"~(P ^ Q)","formula_b":"~P v ~Q"}' \
  localhost:50051 sislogica.parser.v1.LogicParserService/ValidateEquivalence
```
Resposta esperada: `equivalent: true`, `variables: ["P", "Q"]`, `counterexample` omitido/vazio e 4 linhas em `truthTable` todas com `equivalent: true`.

### Exemplo 2 — Fórmulas não equivalentes com contraexemplo (`P -> Q` vs `Q -> P`)
```bash
grpcurl -plaintext -d '{"formula_a":"P -> Q","formula_b":"Q -> P"}' \
  localhost:50051 sislogica.parser.v1.LogicParserService/ValidateEquivalence
```
Resposta esperada:
```json
{
  "variables": ["P", "Q"],
  "counterexample": {
    "P": true,
    "Q": false
  },
  "truthTable": [
    { "valuation": { "P": true,  "Q": true  }, "resultA": true,  "resultB": true,  "equivalent": true },
    { "valuation": { "P": true,  "Q": false }, "resultB": true },
    { "valuation": { "P": false, "Q": true  }, "resultA": true },
    { "valuation": { "P": false, "Q": false }, "resultA": true,  "resultB": true,  "equivalent": true }
  ]
}
```
*(Nota do Proto3: campos `bool` com valor `false` são omitidos na representação JSON padrão do Protobuf.)*

---

## 5. Tratamento de Erros (Códigos gRPC)

Erros do domínio puro (`internal/domain/errors.go`, verificados via `errors.Is` e encapsulados com `%w`) são traduzidos pelo adaptador gRPC em códigos padronizados:

| Situação | Código gRPC | Exemplo |
|---|---|---|
| Expressão vazia, parênteses desbalanceados, sintaxe inválida ou > 12 variáveis | `INVALID_ARGUMENT` | `(P -> Q`, `P ^^ Q`, `""` |
| Prazo da requisição (`deadline`) expirado durante o cálculo | `DEADLINE_EXCEEDED` | Chamada com timeout menor que o tempo de avaliação |
| Requisição cancelada pelo cliente | `CANCELED` | Cliente desconecta antes da resposta |
| Erro interno inesperado | `INTERNAL` | Falha não mapeada |
