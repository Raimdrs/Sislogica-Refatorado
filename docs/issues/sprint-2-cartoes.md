# Cartões da Sprint 2 — Sílógica (GitHub Issues / Projects)

Enunciados adaptados ao monorepo **Sílógica** (`api/`, `services/parser/`, `protos/`, `docs/`) prontos para criar as **Issues** no repositório e vinculá-las ao quadro do **GitHub Projects**.

> **Regra para pontuação de Atividade no Repositório:**  
> 1. Cada cartão abaixo deve ser criado como **Issue** no repositório e adicionado ao GitHub Project da equipe.  
> 2. Atribuir um responsável (**Assignee**) a cada Issue.  
> 3. No Pull Request que resolve a tarefa, incluir `Closes #<numero-da-issue>` na descrição para fechar e vincular o cartão automaticamente no merge.

---

## [T1] Quebrar o trabalho da Sprint 2 em cartões no quadro

**Labels:** `sprint-2`, `gestao`  
**Critério da rubrica:** Atividade no repositório (30%)  
**Responsável:** *A definir*

### Objetivo
Planejar a Sprint 2 no GitHub Projects e garantir rastreabilidade entre cartões, responsáveis e Pull Requests.

### O que fazer
- [ ] Criar as Issues das tarefas T2 a T12 no repositório `Raimdrs/Sislogica-Refatorado`
- [ ] Adicionar todas as Issues ao quadro no GitHub Projects (colunas: *To Do*, *In Progress*, *In Review*, *Done*)
- [ ] Atribuir um responsável a cada cartão e garantir que cada integrante tenha ao menos 1 PR integrado e revise PRs dos colegas
- [ ] Vincular cada PR ao seu cartão usando `Closes #<id>`

### Pronto quando
O quadro mostra todo o escopo da Sprint 2 com donos definidos e cada cartão fechado aponta para um PR integrado na `main`.

---

## [T2] Escolher e justificar a responsabilidade do serviço Go (ADR)

**Labels:** `sprint-2`, `go`, `arquitetura`, `docs`  
**Critério da rubrica:** Microsserviço Go (25%) · Documentação (10%)  
**Responsável:** *A definir*

### Objetivo
Definir uma responsabilidade clara do domínio do Sílógica para o microsserviço em Go e documentar a decisão arquitetural em ADR.

### O que fazer
- [ ] Alinhar com a equipe a responsabilidade única do serviço Go (ex.: motor de análise/validação e redução de silogismos com processamento em lote concorrente, ou motor de parser AST/tabela-verdade/equivalências proposicionais)
- [ ] Confirmar que o serviço Go **não** acessa nem compartilha tabelas do PostgreSQL da API Quarkus
- [ ] Criar a ADR em `docs/decisoes/001-servico-go.md` no formato: **Contexto**, **Decisão**, **Alternativas consideradas** e **Consequências** (explicitando o que o serviço faz, por que fica fora da API e o que aconteceria se ficasse dentro)

### Pronto quando
A ADR está em `docs/decisoes/` e a responsabilidade do serviço Go cabe em uma frase clara e tecnicamente justificada.

---

## [T3] Escrever o domínio do serviço Go, com testes

**Labels:** `sprint-2`, `go`, `testes`  
**Critério da rubrica:** Microsserviço Go (25%)  
**Responsável:** *A definir*

### Objetivo
Implementar a lógica de domínio do microsserviço em Go puro, idiomático e testado, totalmente desacoplado de rede/gRPC.

### O que fazer
- [ ] Organizar o módulo em `services/parser/` (ou diretório do serviço Go) separando o pacote de domínio puro (ex.: `internal/domain/`)
- [ ] Declarar interfaces pequenas onde forem usadas (sem acoplar a gRPC ou HTTP)
- [ ] Receber `context.Context` como primeiro parâmetro em funções que podem esperar/processar e respeitar cancelamento/prazo (`select` com `ctx.Done()`)
- [ ] Declarar erros nomeados (sentinela) compatíveis com `errors.Is` e adicionar contexto usando `fmt.Errorf("...: %w", err)`
- [ ] Escrever testes unitários com `go test ./...`, incluindo teste explícito do comportamento sob prazo expirado (`context.WithTimeout` / `context.WithCancel`)

### Pronto quando
`go test -v ./...` passa 100%, cobre o comportamento sob prazo/cancelamento e o pacote de domínio não importa gRPC nem HTTP.

---

## [T4] Escrever o contrato `.proto`, configurar `buf lint` e gerar stubs

**Labels:** `sprint-2`, `protobuf`, `grpc`  
**Critério da rubrica:** Contrato Protobuf e integração gRPC (25%)  
**Responsável:** *A definir*

### Objetivo
Formalizar o contrato gRPC versionado em `protos/`, validado por `buf lint`, e gerar os stubs automaticamente para Go (e preparar geração no Quarkus).

### O que fazer
- [ ] Configurar `protos/buf.yaml` com regras `STANDARD` e `COMMENTS` e `protos/buf.gen.yaml` para geração dos stubs Go
- [ ] Atualizar/criar o contrato em `protos/sislogica/<servico>/v1/<arquivo>.proto` com pacote versionado (`sislogica.<servico>.v1`) e **comentários em todos os serviços, RPCs, mensagens e campos**
- [ ] Rodar `buf lint` até passar sem avisos/erros
- [ ] Gerar os stubs Go com `buf generate` (nunca escrever stubs manualmente)

### Pronto quando
`buf lint` termina com código 0 sem saída de erro e os stubs Go gerados compilam corretamente.

---

## [T5] Expor o serviço Go por gRPC

**Labels:** `sprint-2`, `go`, `grpc`  
**Critério da rubrica:** Contrato Protobuf e integração gRPC (25%)  
**Responsável:** *A definir*

### Objetivo
Subir o servidor gRPC do microsserviço Go sobre o domínio puro, com tratamento de erros, observabilidade básica e encerramento gracioso.

### O que fazer
- [ ] Implementar o adaptador gRPC traduzindo entre mensagens Protobuf e o domínio, embutindo `Unimplemented...Server`
- [ ] Traduzir erros do domínio (`errors.Is`) em códigos de status gRPC adequados (`INVALID_ARGUMENT`, `DEADLINE_EXCEEDED`, `NOT_FOUND`, etc.)
- [ ] Registrar **gRPC Health Check** (`grpc_health_v1`, respondendo `SERVING`) e **gRPC Reflection**
- [ ] Implementar encerramento gracioso com `GracefulStop` ao receber `SIGTERM`/`SIGINT`
- [ ] Escrever testes do adaptador pela pilha gRPC usando `bufconn`
- [ ] Conferir listagem, chamada RPC e health check com `grpcurl`

### Pronto quando
Os testes com `bufconn` passam, o `grpcurl` lista o serviço e executa chamadas com sucesso, e o health check retorna `SERVING`.

---

## [T6] Chamar o serviço Go a partir da API Quarkus

**Labels:** `sprint-2`, `quarkus`, `grpc`, `arquitetura`  
**Critério da rubrica:** Contrato Protobuf e integração gRPC (25%)  
**Responsável:** *A definir*

### Objetivo
Integrar a API Quarkus (`api/`) ao microsserviço Go por gRPC mantendo Clean Architecture, prazo obrigatório e degradação graciosa.

### O que fazer
- [ ] Declarar a porta de saída no domínio da API (`api/src/main/java/com/sislogica/api/domain/...`) sem nenhum import de gRPC/Protobuf
- [ ] Adicionar `quarkus-grpc` no `api/pom.xml` gerando os stubs Java no build a partir de `protos/`
- [ ] Implementar o adaptador cliente gRPC em `infrastructure/` chamando o Go **sempre com prazo** (`withDeadlineAfter`) e tratando `UNAVAILABLE`, `DEADLINE_EXCEEDED` e `INVALID_ARGUMENT`
- [ ] Implementar degradação graciosa: quando o serviço Go estiver indisponível, a API continua respondendo dentro do prazo sem erro 500
- [ ] Criar teste automatizado cobrindo o fluxo do cliente gRPC contra um servidor gRPC (em memória ou container Testcontainers)
- [ ] Atualizar `ArchitectureTest.java` para proibir `io.grpc..` e o pacote dos stubs gerados na camada `..domain..`

### Pronto quando
Com o Go ativo, a API integra via gRPC; com o Go parado, a API responde normalmente dentro do prazo; teste automatizado do fluxo e `ArchitectureTest` passam.

---

## [T7] Completar os testes que saíram da Sprint 1

**Labels:** `sprint-2`, `quarkus`, `testes`, `qa`  
**Critério da rubrica:** Ambiente e testes (15%)  
**Responsável:** *A definir*

### Objetivo
Entregar a suíte completa do serviço principal (`api/`): unitários do domínio/serviço, integração real com PostgreSQL e teste de arquitetura blindado.

### O que fazer
- [ ] Revisar testes unitários de domínio e casos de uso (sem banco, com repositórios falsos/em memória)
- [ ] Expandir `TurmaResourceTest` e `SilogismoResourceTest` (`@QuarkusTest` + Dev Services/PostgreSQL) para cobrir **CRUD completo** (`POST` com `201` + `Location`, `GET`, `PUT`, `DELETE`), **relacionamento 1:N** (`GET /api/v1/turmas/{id}/silogismos`), **paginação real** (`pagina`/`tamanho`), **filtros** e erros `ProblemDetails` (`400`/`404`)
- [ ] Fortalecer `ArchitectureTest.java` garantindo que `..domain..` não importa framework, banco, `..controller..`, `..dto..`, `..service..` nem `io.grpc..` (e validar propositalmente que falha se violar)
- [ ] Remover acoplamentos residuais (ex.: métodos `fromEntity` não usados nos DTOs importando `PanacheEntity`)
- [ ] Conferir execução com `./mvnw test` / `mise run test` sem ajustes manuais

### Pronto quando
Os três tipos de teste (unitários, integração com PostgreSQL real e ArchUnit) existem e passam local e no CI sem alterar configuração.

---

## [T8] Subir tudo com `docker compose`

**Labels:** `sprint-2`, `docker`, `devops`  
**Critério da rubrica:** Ambiente e testes (15%)  
**Responsável:** *A definir*

### Objetivo
Permitir subir PostgreSQL, microsserviço Go e API Quarkus do zero com um único comando em qualquer máquina com Docker.

### O que fazer
- [ ] Criar `Dockerfile` multi-stage para o microsserviço Go (imagem final mínima + suporte a healthcheck)
- [ ] Criar `Dockerfile` multi-stage para a API Quarkus (`api/Dockerfile`)
- [ ] Garantir endpoint/comando de health check em todos os serviços (Postgres, Go e API `/health`)
- [ ] Atualizar `docker-compose.yml` (ou `compose.yaml`) com `postgres`, serviço Go e `api`, usando `healthcheck`, `depends_on: condition: service_healthy` e variáveis de ambiente com valores padrão (para funcionar num clone limpo sem exigir `.env` manual)

### Pronto quando
Em um clone limpo, `docker compose up --build` sobe os 3 serviços sem passos manuais e `docker compose ps` mostra todos saudáveis (`healthy`).

---

## [T9] Ampliar o CI no GitHub Actions

**Labels:** `sprint-2`, `ci`, `devops`  
**Critério da rubrica:** Contrato Protobuf e gRPC (25%) · Ambiente e testes (15%)  
**Responsável:** *A definir*

### Objetivo
Garantir que o pipeline de CI valide automaticamente contrato Protobuf, microsserviço Go e suíte completa da API em todo push e PR.

### O que fazer
- [ ] Adicionar job/step de `buf lint` (ex.: `bufbuild/buf-action`) em `.github/workflows/ci.yml`
- [ ] Garantir execução de `go test -v ./...` e build do serviço Go
- [ ] Garantir execução da suíte completa da API Quarkus (unitários + integração com Dev Services/Postgres + gRPC + ArchUnit)
- [ ] *(Opcional)* Publicar imagens Docker no GHCR em pushes na `main`

### Pronto quando
Em push e PR, o workflow de CI roda `buf lint`, testes Go e suíte completa da API e fica verde na branch `main`.

---

## [T10] Pôr a API no ar com banco gerenciado (Neon) + ADR de implantação

**Labels:** `sprint-2`, `deploy`, `devops`, `docs`  
**Critério da rubrica:** Sistema no ar (25%) · Documentação (10%)  
**Responsável:** *A definir*

### Objetivo
Implantar a API Quarkus conectada a um PostgreSQL gerenciado (Neon) com migrações automatizadas, segredos seguros e evidências documentadas.

### O que fazer
- [ ] Provisionar banco PostgreSQL no **Neon** e configurar credenciais/connection string como segredos na plataforma (nunca no repositório)
- [ ] Implantar a API via container (ex.: Render ou similar) com health check configurado (`/health`)
- [ ] Configurar aplicação automática das migrações Flyway por task ou CI (não rodar SQL manualmente)
- [ ] Verificar e evidenciar que a API continua respondendo mesmo sem o serviço Go acessível
- [ ] Adicionar a URL pública no `README.md` (se ativa) e/ou registrar evidências da implantação (workflow, logs, prints)
- [ ] Escrever a ADR de implantação em `docs/decisoes/002-implantacao.md` (plataforma, Neon, segredos, migrações e motivo de o serviço Go ficar fora do ar ou como foi publicado)

### Pronto quando
A implantação está demonstrada com evidências (e URL no README, se houver), sem segredos no repositório, e a ADR está em `docs/decisoes/`.

---

## [T11] Documentar o sistema (README, OpenAPI, ADRs, Licença e Uso de IA)

**Labels:** `sprint-2`, `docs`  
**Critério da rubrica:** Documentação (10%)  
**Responsável:** *A definir*

### Objetivo
Garantir que qualquer avaliador consiga entender, subir e testar todo o sistema em menos de 10 minutos apenas seguindo o repositório.

### O que fazer
- [ ] Atualizar `README.md` com todas as seções exigidas: O que é, Arquitetura (diagrama API + Go + Postgres + REST/gRPC/SQL), No ar, Pré-requisitos com versões, Como rodar (`docker compose up --build` + envs), Como testar (API e Go), 2–3 exemplos `curl` com resposta esperada, Licença e link do Vídeo
- [ ] Revisar documentação OpenAPI (`/q/swagger-ui` e `/q/openapi`) cobrindo todas as rotas, tipos de parâmetros e respostas de erro (`ProblemDetails`)
- [ ] Conferir as duas ADRs em `docs/decisoes/` (serviço Go e implantação)
- [ ] Adicionar arquivo `LICENSE` (ex.: MIT) na raiz do repositório e referenciá-lo no `README.md`
- [ ] Criar/atualizar `docs/uso-de-ia.md` com o registro de uso de IA da equipe
- [ ] Validar cronometrando a subida em um clone limpo (< 10 minutos)

### Pronto quando
README, OpenAPI, 2 ADRs, `LICENSE` e `docs/uso-de-ia.md` estão completos e validados no repositório.

---

## [T12] Gravar o vídeo de 5 minutos da Sprint 2

**Labels:** `sprint-2`, `apresentacao`  
**Critério da rubrica:** Comunicação (20%)  
**Responsável:** **Equipe (Moab, Raí e Samuel)**

### Objetivo
Demonstrar o produto final funcionando ao vivo e justificar as decisões técnicas tomadas pela equipe.

### O que fazer
- [ ] Seguir o roteiro de 5 minutos:
  - `0:00–0:30` — O que a sprint entregou e a responsabilidade do serviço Go
  - `0:30–2:00` — Sistema implantado (requisição real ou logs/evidências) e API respondendo sem o Go
  - `2:00–3:00` — Contrato `.proto`, `buf lint` e chamada gRPC com prazo (API ou `grpcurl`)
  - `3:00–4:00` — `docker compose up` do zero e serviços saudáveis (`docker compose ps`)
  - `4:00–5:00` — Testes e CI verde + próximos passos
- [ ] Garantir participação falada de **todos os 3 integrantes**
- [ ] Publicar o vídeo e adicionar o link no `README.md`

### Pronto quando
Vídeo (~5 min) publicado, acessível pelo link no `README.md`, cobrindo todo o roteiro com todos os integrantes falando.
