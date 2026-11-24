# Sprint 2 (Entrega Final) — Sílógica

**Disciplina:** DIM0547 — Desenvolvimento de Sistemas Web II (2026.2)  
**Coorte:** Coorte B  
**Prazo de entrega final:** 30/11/2026, 23:59  
**Fase:** Sprint 2 — Microsserviço Go + Contrato Protobuf/gRPC + Sistema no Ar + Testes + Documentação

> Conforme ajuste de cronograma da disciplina (03/10), a Sprint 2 consolida o produto final do semestre (substituindo Sprint 3 e Entrega Final).

---

## 1. Critérios da Rubrica (Entrega Técnica — 50%)

| Critério | Peso | Resumo do que precisa estar pronto |
|---|---:|---|
| **Microsserviço Go** | 25% | Serviço com responsabilidade única e justificada em ADR; Go idiomático, domínio puro, `context.Context` respeitando prazo e erros com `errors.Is` / `%w` |
| **Contrato Protobuf e integração gRPC** | 25% | `.proto` versionado (`v1`) e comentado em `protos/`, `buf lint` verde no CI, stubs gerados; API Quarkus chamando o Go com deadline e tratamento de erro/degradação + teste automatizado |
| **Sistema no ar** | 25% | API e PostgreSQL gerenciado (Neon) implantados em plataforma de container (evidências + URL no README, se houver), migrações por task/CI, segredos fora do repo, health check e API respondendo sem o Go |
| **Ambiente e testes** | 15% | `docker compose up --build` sobe banco, Go e API do zero; testes unitários, de integração com Postgres real (Dev Services) e ArchUnit verdes local e no CI |
| **Documentação** | 10% | README completo (< 10 min para rodar), OpenAPI por URL, 2 ADRs novas em `docs/decisoes/` e `LICENSE` na raiz |

Além da entrega técnica (50%), a nota inclui **Atividade no repositório (30%)** (CI verde, commits distribuídos, ao menos 1 PR integrado por integrante, PRs revisados por outro integrante e cartões ligados a PRs) e **Comunicação (20%)** (vídeo de 5 min com todos falando + acompanhamento).

---

## 2. Quadro de Cartões da Sprint 2 (T1–T12)

Os enunciados completos prontos para criação das Issues / cartões no GitHub Projects estão em [`docs/issues/sprint-2-cartoes.md`](../issues/sprint-2-cartoes.md).

| Cartão | Tarefa | Critério da Rubrica | Responsável | Issue / PR | Status |
|---|---|---|---|---|---|
| **T1** | Quebrar o trabalho em cartões da sprint | Atividade no repositório (30%) | *A definir* | — | Em andamento |
| **T2** | Escolher e justificar a responsabilidade do serviço Go (ADR) | Microsserviço Go (25%) · Docs (10%) | *A definir* | — | A fazer |
| **T3** | Escrever o domínio do serviço Go, com testes | Microsserviço Go (25%) | *A definir* | — | A fazer |
| **T4** | Escrever o contrato `.proto`, `buf lint` e gerar stubs | Contrato Protobuf e gRPC (25%) | *A definir* | — | A fazer |
| **T5** | Expor o serviço Go por gRPC | Contrato Protobuf e gRPC (25%) | *A definir* | — | A fazer |
| **T6** | Chamar o serviço Go a partir da API Quarkus | Contrato Protobuf e gRPC (25%) | *A definir* | — | A fazer |
| **T7** | Completar os testes que saíram da Sprint 1 | Ambiente e testes (15%) | *A definir* | — | A fazer |
| **T8** | Subir tudo com `docker compose` | Ambiente e testes (15%) | *A definir* | — | A fazer |
| **T9** | Ampliar o CI (`buf lint`, `go test`, suíte API) | Contrato (25%) · Testes (15%) | *A definir* | — | A fazer |
| **T10** | Pôr a API no ar com banco gerenciado (Neon) + ADR | Sistema no ar (25%) | *A definir* | — | A fazer |
| **T11** | Documentar o sistema (README, OpenAPI, ADRs, `LICENSE`) | Documentação (10%) | *A definir* | — | A fazer |
| **T12** | Gravar o vídeo de 5 minutos | Comunicação (20%) | **Equipe (todos)** | — | A fazer |

---

## 3. Ordem de Execução e Dependências

```text
Paralelo desde já:
  T1 (Quadro)
  T7 (Testes da Sprint 1: unitários, integração Postgres, ArchUnit)

Caminho crítico (Microsserviço Go + gRPC):
  T2 (Responsabilidade + ADR Go)
   ├──▶ T3 (Domínio Go + testes de prazo) ──┐
   └──▶ T4 (Contrato .proto + buf lint) ────┼──▶ T5 (Servidor gRPC Go) ──┐
                                            └──▶ T6 (Cliente gRPC API) ──┴──▶ T8 (Docker Compose)
                                                                               ├──▶ T9 (CI completo)
                                                                               ├──▶ T10 (Deploy + Neon + ADR)
                                                                               └──▶ T11 (Documentação final)
                                                                                     └──▶ T12 (Vídeo 5 min)
```

---

## 4. Checklist Final da Sprint 2

- [ ] Microsserviço Go com responsabilidade justificada em ADR (`docs/decisoes/`)
- [ ] `protos/` versionado (`v1`), comentado, com `buf lint` verde no CI e stubs gerados
- [ ] Teste de integração gRPC ponta a ponta (com deadline e tratamento de erro)
- [ ] `docker compose up --build` sobe Postgres, Go e API do zero com serviços saudáveis
- [ ] API implantada com banco gerenciado (Neon), com evidências (e URL no README, se disponível); API funciona mesmo sem o serviço Go
- [ ] Migrações aplicadas por task ou CI; nenhum segredo no repositório
- [ ] Testes unitários, de integração (Dev Services / PostgreSQL) e de arquitetura (ArchUnit bloqueando framework, banco e `io.grpc`), verdes local e no CI
- [ ] Documentação completa: `README.md` (< 10 min), OpenAPI por URL, 2 ADRs em `docs/decisoes/`, `LICENSE` na raiz e `docs/uso-de-ia.md`
- [ ] Vídeo de 5 min com todos os integrantes falando e link no `README.md`
