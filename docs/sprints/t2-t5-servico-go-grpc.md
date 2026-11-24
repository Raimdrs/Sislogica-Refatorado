# [T2–T5 / Issue 06] Microsserviço Go + Contrato Protobuf v1 + Servidor gRPC

Guia técnico para a equipe (**Moab, Raí e Samuel**) explicando tudo o que foi implementado no microsserviço Go (`services/parser/`) e no contrato Protobuf (`protos/`), como rodar, como testar com `go test`, `buf lint` e `grpcurl`, e como a **T6** (cliente gRPC na API Quarkus) vai se conectar a ele.

---

## 1. O que foi entregue nesta branch (T2, T3, T4 e T5)

| Tarefa | Entregáveis | Arquivos |
|---|---|---|
| **T2 — Responsabilidade + ADR** | ADR formal justificando por que o motor de parsing AST e avaliação combinatória ($2^n$) fica fora da API Quarkus, sem dividir tabelas do Postgres | [`docs/decisoes/001-servico-go.md`](../decisoes/001-servico-go.md) |
| **T4 — Contrato `.proto` + Buf** | Contrato versionado `sislogica.parser.v1`, 100% comentado, `buf.yaml` (`STANDARD` + `COMMENTS`), `buf.gen.yaml`, `buf lint` verde e stubs Go gerados | [`protos/buf.yaml`](../../protos/buf.yaml), [`protos/buf.gen.yaml`](../../protos/buf.gen.yaml), [`protos/sislogica/parser/v1/parser.proto`](../../protos/sislogica/parser/v1/parser.proto), `services/parser/gen/` |
| **T3 — Domínio Go puro** | Lexer, parser *recursive-descent*, AST JSON e avaliador de equivalências ($2^n$) sem imports de gRPC/HTTP; `context.Context` com `ctx.Done()`; erros sentinela (`errors.Is` + `%w`); testes unitários + teste de prazo | `services/parser/internal/domain/{errors,ast,parser,parser_test}.go` |
| **T5 — Servidor gRPC** | Adaptador com `UnimplementedLogicParserServiceServer`, mapeamento de erros para códigos gRPC (`INVALID_ARGUMENT`, `DEADLINE_EXCEEDED`, `CANCELED`), **gRPC Health Check (`SERVING`)**, **gRPC Reflection**, `GracefulStop` no `SIGTERM`, logs estruturados (`slog`) e testes via `bufconn` | `services/parser/internal/grpcserver/{server,server_test}.go`, `services/parser/main.go` |

---

## 2. Como funciona internamente

```text
Cliente gRPC (Quarkus T6 ou grpcurl)
        │  gRPC / Protobuf (sislogica.parser.v1)
        ▼
internal/grpcserver/server.go   (adaptador gRPC + tradução de erros + Health + Reflection)
        │  interface LogicService (declarada pelo consumidor)
        ▼
internal/domain/                (100% Go puro, zero imports de gRPC/HTTP)
 ├── parser.go                  (Lexer + Parser recursive-descent + ValidateEquivalence 2^n)
 ├── ast.go                     (Nós AST, Evaluate, CollectVariables, limite MaxVariables=12)
 └── errors.go                  (ErrEmptyExpression, ErrUnbalancedParentheses, ErrInvalidSyntax, ErrTooManyVariables)
```

### Operadores suportados
- **Variáveis:** `[A-Z][0-9]*` (ex.: `P`, `Q`, `R`, `P1`)
- **Negação:** `~`, `!`, `¬`
- **Conjunção:** `^`, `&`, `&&`, `∧`
- **Disjunção:** `v` (minúsculo), `|`, `||`, `∨`
- **Implicação (associativa à direita):** `->`, `→`
- **Bicondicional:** `<->`, `↔`
- **Parênteses:** `( )`

### Mapeamento de erros de domínio → códigos gRPC
| Erro de domínio (`errors.Is`) | Código gRPC |
|---|---|
| `ErrEmptyExpression`, `ErrUnbalancedParentheses`, `ErrInvalidSyntax`, `ErrTooManyVariables` | `INVALID_ARGUMENT` |
| `context.DeadlineExceeded` | `DEADLINE_EXCEEDED` |
| `context.Canceled` | `CANCELED` |
| Outros erros inesperados | `INTERNAL` |

---

## 3. Como validar, rodar e testar

### A. Validar contrato e regenerar stubs (Buf)
```bash
cd protos
buf lint
buf generate
```

### B. Rodar todos os testes em Go (domínio + prazo + servidor gRPC via `bufconn`)
```bash
cd services/parser
go test -v ./...
```

### C. Subir o servidor gRPC localmente
```bash
cd services/parser
go run main.go
# Escuta na porta 50051 por padrão (configurável via GRPC_PORT=50051)
```

### D. Testar com `grpcurl` (em outro terminal)
```bash
# 1. Listar serviços via Reflection
grpcurl -plaintext localhost:50051 list

# 2. Verificar Health Check (deve retornar SERVING)
grpcurl -plaintext -d '{"service":"sislogica.parser.v1.LogicParserService"}' \
  localhost:50051 grpc.health.v1.Health/Check

# 3. Chamar ParseExpression
grpcurl -plaintext -d '{"expression":"(P & Q) -> R"}' \
  localhost:50051 sislogica.parser.v1.LogicParserService/ParseExpression

# 4. Chamar ValidateEquivalence (Lei de De Morgan)
grpcurl -plaintext -d '{"formula_a":"~(P ^ Q)","formula_b":"~P v ~Q"}' \
  localhost:50051 sislogica.parser.v1.LogicParserService/ValidateEquivalence

# 5. Chamar ValidateEquivalence com contraexemplo (P -> Q vs Q -> P)
grpcurl -plaintext -d '{"formula_a":"P -> Q","formula_b":"Q -> P"}' \
  localhost:50051 sislogica.parser.v1.LogicParserService/ValidateEquivalence
```

---

## 4. Próximo passo (T6 — integração no Quarkus)

Na **T6**, a API Quarkus (`api/`) vai:
1. Adicionar `quarkus-grpc` gerando stubs Java automaticamente no pacote `com.sislogica.parser.v1` a partir de `protos/sislogica/parser/v1/parser.proto`;
2. Declarar uma porta pura no domínio da API e implementar o adaptador cliente gRPC na infraestrutura com prazo obrigatório (`withDeadlineAfter`);
3. Tratar `UNAVAILABLE`, `DEADLINE_EXCEEDED` e `INVALID_ARGUMENT` com **degradação graciosa** (fallback local quando o serviço Go não estiver acessível);
4. Adicionar `"com.sislogica.parser.v1.."` às regras proibidas no `ArchitectureTest.java`.
