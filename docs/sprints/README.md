# Sprints — Sílógica

Documentação de sprints e acompanhamento do projeto para a disciplina **DIM0547 - Desenvolvimento de Sistemas Web II (2026.2)** — **Coorte B**.

---

## Índice de Sprints

- [Sprint 0 — Planejamento & Arquitetura Inicial](sprint-0.md)
- *Sprint 1 — MVP Core: CRUD de Exercícios, Resolução de Tabelas-Verdade & Persistência*
- *Sprint 2 — Microsserviço Go (gRPC AST Parser) & Validações Avançadas*
- *Sprint 3 — Cache (Caffeine/Redis), Observabilidade & Hardening*
- *Bloco Final — Autenticação JWT, Histórico do Aluno & Apresentação*

---

## Escopo Geral do Produto (In / Out)

### Escopo do MVP (Sprint 1)
- CRUD completo das entidades principais (Exercícios, Proposições, Regras) via API REST no Quarkus.
- Módulo algorítmico de geração e validação de **Tabelas-Verdade** para fórmulas proposicionais ($P, Q, \land, \lor, \neg, \rightarrow, \leftrightarrow$).
- Validador de equivalências lógicas básicas.
- Persistência em banco de dados relacional **PostgreSQL** com versionamento via **Flyway**.
- Módulo de submissão e correção automática de exercícios.
- Documentação interativa via **OpenAPI / Swagger UI**.
- Infraestrutura de testes com **Testcontainers** e pipeline de CI.

### Fica Fora do MVP (Sprints Futuras / Bloco Final)
- Frontend / Interface Web gráfica (o consumo é exclusivamente via API REST / Swagger / cURL / Postman no MVP).
- Gerador de argumentos complexos por Inteligência Artificial.
- Relatórios gerenciais complexos.
- Autenticação e Perfis (JWT/OAuth2) — previsto para o Bloco Final.
- Fórum de dúvidas integrado entre alunos.

---

## Organização das Issues

As especificações detalhadas das histórias de usuário e tarefas técnicas estão organizadas em [`docs/issues/`](../issues/). Cada issue contém contexto, critérios de aceite, tasks técnicas e testes exigidos.
