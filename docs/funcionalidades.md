# Funcionalidades da API — Sílógica

Guia das funcionalidades disponíveis na API REST (`api/`), explicando **como funcionam** e **como acessá-las** via `curl` ou Swagger UI (`http://localhost:8080/q/swagger-ui/`).

> Todas as respostas de erro de validação ou regra de negócio seguem o padrão **Problem Details (RFC 9457)** com `Content-Type: application/problem+json`.

---

## 1. Saúde, Documentação e Observabilidade

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/health` | Verifica status da API (`status: UP`, nome e versão) |
| `GET` | `/q/swagger-ui/` | Interface interativa OpenAPI / Swagger UI |
| `GET` | `/q/openapi` | Especificação OpenAPI em YAML/JSON |
| `GET` | `/q/metrics` | Métricas Micrometer / Prometheus (incluindo acertos/erros de cache Caffeine) |

---

## 2. Gestão de Turmas (`/api/v1/turmas`)

Permite cadastrar, listar, consultar, atualizar e remover turmas/classes de lógica (lado `1` do relacionamento `1:N` com Silogismos).

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/api/v1/turmas` | Cria nova turma com `codigo` único (`201 Created` + header `Location`) |
| `GET` | `/api/v1/turmas?pagina=0&tamanho=10&professor=...` | Listagem paginada no PostgreSQL com filtro opcional por professor |
| `GET` | `/api/v1/turmas/{id}` | Busca turma por ID (`200 OK` / `404 Not Found`) — com cache Caffeine (`turmas`) |
| `PUT` | `/api/v1/turmas/{id}` | Atualiza dados da turma e invalida cache (`200 OK` / `400` / `404`) |
| `DELETE` | `/api/v1/turmas/{id}` | Remove turma em cascata e invalida caches (`204 No Content` / `404`) |
| `GET` | `/api/v1/turmas/{turmaId}/silogismos?pagina=0&tamanho=10` | **Rota aninhada** listando os silogismos vinculados à turma (`1:N`) |

---

## 3. Gestão de Silogismos Aristotélicos (`/api/v1/silogismos`)

Permite gerenciar silogismos compostos por três proposições categóricas (`premissaMaior`, `premissaMenor`, `conclusao`), cada uma com:
- `quantificador`: `A` (Universal Afirmativa), `E` (Universal Negativa), `I` (Particular Afirmativa) ou `O` (Particular Negativa);
- `sujeito` e `predicado`: `T` (Termo Maior), `t` (Termo Menor) ou `M` (Termo Médio);
- `transliteracao` *(opcional)*: texto em linguagem natural.

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/api/v1/silogismos` | Cria silogismo vinculado a uma `turmaId` existente (`201 Created` + `Location`) |
| `GET` | `/api/v1/silogismos?pagina=0&tamanho=10&modo=...` | Listagem paginada no PostgreSQL com filtro opcional por modo (`detalhe.modo`) |
| `GET` | `/api/v1/silogismos/{id}` | Busca silogismo por ID (`200 OK` / `404`) — com cache Caffeine (`silogismos`) |
| `PUT` | `/api/v1/silogismos/{id}` | Atualiza proposições/título/turma e invalida cache (`200 OK` / `400` / `404`) |
| `DELETE` | `/api/v1/silogismos/{id}` | Remove silogismo e invalida cache (`204 No Content` / `404`) |

---

## 4. Validador de Equivalências Lógicas (`/api/v1/equivalencia/validar`) — *Issue 03*

Permite verificar formalmente se duas fórmulas proposicionais são semanticamente equivalentes ($A \equiv B$), comparando suas tabelas-verdade sob todas as valorações possíveis e retornando o primeiro contraexemplo em caso de divergência.

### Como funciona internamente (Clean Architecture)
1. **Análise léxica e sintática (`ParserProposicional`):** transforma `formulaA` e `formulaB` em árvores sintáticas abstratas (`FormulaProposicional` — AST) respeitando a precedência formal dos operadores lógicos.
2. **Unificação de variáveis (`ValidadorEquivalenciaDomainService`):** reúne todas as variáveis proposicionais presentes nas duas fórmulas em um conjunto ordenado alfabeticamente (ex.: `P` e `P v (P ^ Q)` → `["P", "Q"]`), com limite de segurança de até **12 variáveis distintas** ($2^{12} = 4096$ linhas).
3. **Comparação das tabelas-verdade:** avalia as duas ASTs em todas as $2^n$ combinações de valores-verdade (começando de `true` até `false`).
4. **Resultado e contraexemplo:** se todas as linhas coincidirem, retorna `equivalentes: true` e `contraexemplo: null`; caso contrário, retorna `equivalentes: false` e a primeira valoração em que `resultadoA != resultadoB`.

### Operadores e sintaxe suportados (da maior para a menor precedência)
- **Variáveis:** letra maiúscula opcionalmente seguida de dígitos (`P`, `Q`, `R`, `A`, `B`, `P1`).
- **Parênteses:** `( )`
- **Negação (NOT):** `~`, `!`, `¬` *(ex.: `~P`, `~~P`)*
- **Conjunção (AND):** `^`, `&`, `&&`, `∧` *(ex.: `P ^ Q`)*
- **Disjunção (OR):** `v` *(minúsculo)*, `|`, `||`, `∨` *(ex.: `~P v ~Q`)*
- **Implicação (IMPLIES, associativa à direita):** `->`, `→` *(ex.: `P -> Q`)*
- **Bicondicional (IFF):** `<->`, `↔` *(ex.: `P <-> Q`)*

### Como acessar

#### Exemplo 1 — Fórmulas equivalentes (Lei de De Morgan: `~(P ^ Q)` vs `~P v ~Q`)
```bash
curl -X POST http://localhost:8080/api/v1/equivalencia/validar \
  -H "Content-Type: application/json" \
  -d '{
    "formulaA": "~(P ^ Q)",
    "formulaB": "~P v ~Q"
  }'
```

Resposta (`200 OK`):
```json
{
  "formulaA": "~(P ^ Q)",
  "formulaB": "~P v ~Q",
  "equivalentes": true,
  "variaveis": ["P", "Q"],
  "contraexemplo": null,
  "tabelaVerdade": [
    { "valoracao": { "P": true,  "Q": true  }, "resultadoA": false, "resultadoB": false, "equivalente": true },
    { "valoracao": { "P": true,  "Q": false }, "resultadoA": true,  "resultadoB": true,  "equivalente": true },
    { "valoracao": { "P": false, "Q": true  }, "resultadoA": true,  "resultadoB": true,  "equivalente": true },
    { "valoracao": { "P": false, "Q": false }, "resultadoA": true,  "resultadoB": true,  "equivalente": true }
  ]
}
```

#### Exemplo 2 — Fórmulas não equivalentes com contraexemplo (`P -> Q` vs `Q -> P`)
```bash
curl -X POST http://localhost:8080/api/v1/equivalencia/validar \
  -H "Content-Type: application/json" \
  -d '{
    "formulaA": "P -> Q",
    "formulaB": "Q -> P"
  }'
```

Resposta (`200 OK`):
```json
{
  "formulaA": "P -> Q",
  "formulaB": "Q -> P",
  "equivalentes": false,
  "variaveis": ["P", "Q"],
  "contraexemplo": { "P": true, "Q": false },
  "tabelaVerdade": [
    { "valoracao": { "P": true,  "Q": true  }, "resultadoA": true,  "resultadoB": true,  "equivalente": true },
    { "valoracao": { "P": true,  "Q": false }, "resultadoA": false, "resultadoB": true,  "equivalente": false },
    { "valoracao": { "P": false, "Q": true  }, "resultadoA": true,  "resultadoB": false, "equivalente": false },
    { "valoracao": { "P": false, "Q": false }, "resultadoA": true,  "resultadoB": true,  "equivalente": true }
  ]
}
```

#### Exemplo 3 — Erro sintático (`(P -> Q` sem fechar parêntese)
Retorna **`400 Bad Request`** no padrão **Problem Details (`application/problem+json`)** indicando a posição do erro sintático.
