# [Issue 06] Microsserviço em Go para Parser Formal e Comunicação gRPC

## Contexto
Para processar fórmulas proposicionais complexas de forma escalável e com baixo uso de memória, criar um microsserviço dedicado em Go que realiza a análise léxica e sintática (parser) e expõe um contrato gRPC para o serviço Quarkus.

## Critérios de Aceite
- [x] Definição do arquivo `.proto` versionado (`protos/sislogica/parser/v1/parser.proto`) validado com `buf lint` (`STANDARD` e `COMMENTS`) contendo:
  - Mensagens `ParseExpressionRequest` / `ParseExpressionResponse` (com `valid`, `variables` e `ast_json`) e `ValidateEquivalenceRequest` / `ValidateEquivalenceResponse`
  - Serviço `LogicParserService` (`sislogica.parser.v1`).
- [x] Microsserviço em Go que implementa o servidor gRPC, Health Check (`SERVING`), Reflection, `GracefulStop` e o parser formal + avaliador de equivalências no domínio puro.
- [ ] Cliente gRPC no Quarkus utilizando `quarkus-grpc` para invocar o serviço Go em requisições de parsing avançado *(T6)*.
- [ ] Tratamento de falhas de comunicação com fallback e deadline obrigatório *(T6)*.

## Tasks Técnicas
- [x] Modelar o schema protobuf versionado (`v1`), configurar `buf.yaml`/`buf.gen.yaml` e gerar stubs Go via `buf generate`.
- [x] Implementar lexer e parser formal em Go (recursive-descent com `context.Context`, erros nomeados e AST).
- [x] Configurar servidor gRPC em Go com logs estruturados (`slog`), Health Check, Reflection e `GracefulStop`.
- [ ] Integrar cliente gRPC no Quarkus no serviço `ParserClientService` *(T6)*.

## Testes Exigidos
- [x] Testes unitários do parser em Go cobrindo fórmulas malformadas (ex: `P ^^ Q`, `(P -> Q`), equivalências, contraexemplos, prazo/cancelamento (`context`) e pilha gRPC via `bufconn`.
- [ ] Testes de integração gRPC entre Quarkus e o serviço Go *(T6)*.

## Labels / Estimativa
`~microservice` `~golang` `~grpc` · Estimativa: 8 pts · Prioridade: **P3** · Sprint: **Sprint 2**
