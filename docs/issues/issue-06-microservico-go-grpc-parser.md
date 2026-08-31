# [Issue 06] Microsserviço em Go para Parser Formal e Comunicação gRPC

## Contexto
Para processar fórmulas proposicionais complexas de forma escalável e com baixo uso de memória, criar um microsserviço dedicado em Go que realiza a análise léxica e sintática (parser) e expõe um contrato gRPC para o serviço Quarkus.

## Critérios de Aceite
- [ ] Definição do arquivo `.proto` (ex: `formula_parser.proto`) contendo:
  - Mensagem `ParseRequest { string formula = 1; }`
  - Mensagem `ParseResponse { bool is_valid = 1; string error_message = 2; repeated string variables = 3; AstNode root = 4; }`
  - Serviço `FormulaParserService`.
- [ ] Microsserviço em Go que implementa o servidor gRPC e o parser formal de lógica proposicional.
- [ ] Cliente gRPC no Quarkus utilizando `quarkus-grpc` para invocar o serviço Go em requisições de parsing avançado.
- [ ] Tratamento de falhas de comunicação com fallback e circuit breaker / retry.

## Tasks Técnicas
- [ ] Modelar o schema protobuf e gerar stubs para Go e Java.
- [ ] Implementar lexer e parser formal em Go (gramática LL(1) / Pratt parser ou Shunting-yard em Go).
- [ ] Configurar servidor gRPC em Go com logs estruturados.
- [ ] Integrar cliente gRPC no Quarkus no serviço `ParserClientService`.

## Testes Exigidos
- [ ] Testes unitários do parser em Go cobrindo fórmulas malformadas (ex: `P ^^ Q`, `(P -> Q`).
- [ ] Testes de integração gRPC entre Quarkus e o serviço Go.

## Labels / Estimativa
`~microservice` `~golang` `~grpc` · Estimativa: 8 pts · Prioridade: **P3** · Sprint: **Sprint 2**
