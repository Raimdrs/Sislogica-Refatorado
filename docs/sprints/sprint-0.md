# Sprint 0 — Planejamento e Arquitetura: Sílógica

**Disciplina:** DIM0547 - Desenvolvimento de Sistemas Web II (2026.2)  
**Coorte:** Coorte B  
**Fase:** Sprint 0 (Planejamento, Definição de MVP, Arquitetura e Configuração de Ambiente)

---

## 1. Entregáveis da Sprint 0

- [x] **Proposta do Projeto** — [`docs/proposta.md`](../proposta.md) (visão do produto, MVP, escolha tecnológica Quarkus/Go, divisão de responsabilidades, entidades do domínio, equipe).
- [x] **Backlog Inicial** — [`docs/backlog.md`](../backlog.md) com histórias de usuário priorizadas (P1, P2, P3), critérios de aceite e estimativas.
- [x] **Mapeamento de Issues** — [`docs/issues/`](../issues/) com especificações técnicas, critérios de aceite e tarefas de desenvolvimento.
- [x] **Definição de Arquitetura & Stack** — Java com Quarkus (Serviço Principal) + Go (Microsserviço de Parser/AST via gRPC) + PostgreSQL.
- [ ] **Scaffold do Projeto & CI/CD** — Estrutura base de microsserviços e pipeline de testes automatizados com Testcontainers.
- [ ] **Gravação do Vídeo de Apresentação (5 min)** — Apresentação da equipe, escopo e escolhas arquiteturais.

---

## 2. Escolhas Técnicas e Justificativas

- **Serviço Principal (Java / Quarkus):** Framework Cloud-Native de alta performance com baixo consumo de memória e tempo de inicialização ultrarrápido; suporte nativo a CDI, Flyway, Hibernate ORM / Panache, Swagger/OpenAPI e Testcontainers.
- **Microsserviço Especializado (Go):** Alto desempenho e eficiência de CPU para análise sintática formal (parser léxico e sintático) de proposições lógicas complexas e geração de AST, integrado ao Quarkus via **gRPC**.
- **Persistência de Dados (PostgreSQL + Flyway):** Banco relacional com migrações versionadas para garantir integridade e evolução segura do esquema de dados.
- **Observabilidade e Cache:** Cache Caffeine/Redis e métricas via Micrometer/Prometheus para respostas instantâneas a consultas recorrentes.

---

## 3. Roteiro do Vídeo de Apresentação (5 minutos)

| Tempo Estimado | Conteúdo / Tópico | Responsável Sugerido |
|---|---|---|
| 0:00 - 0:45 | **Apresentação da Equipe & Papéis:** Membros, papéis e contexto da disciplina (DIM0547 - Coorte B). | Integrantes |
| 0:45 - 2:00 | **Visão do Produto & Problema:** Dificuldade no aprendizado de lógica formal e proposta de valor do Sílógica. | Integrante 1 |
| 2:00 - 3:15 | **Definição do MVP (In/Out):** O que entra na Sprint 1 (Tabelas-Verdade, CRUD de Exercícios, validação via API) e o que fica fora. | Integrante 2 |
| 3:15 - 4:15 | **Arquitetura & Escolhas Técnicas:** Por que Quarkus no serviço principal e Go para parser via gRPC. | Integrante 3 |
| 4:15 - 5:00 | **Backlog, Riscos & Próximos Passos:** Demonstração do backlog priorizado e preparação para a Sprint 1. | Integrante 4 |

---

## 4. Riscos Identificados e Mitigações

| Risco Identificado | Impacto | Estratégia de Mitigação |
|---|---|---|
| Complexidade no parser de operadores lógicos com múltiplos aninhamentos | Médio | Iniciar com operadores proposicionais básicos ($\land, \lor, \neg, \rightarrow$) e isolar o analisador léxico/sintático no microsserviço Go com testes unitários exaustivos. |
| Overhead de comunicação gRPC entre Quarkus e Go | Baixo | Uso de buffers proto3 bem tipados e manter as operações simples locais no Quarkus, acionando o Go apenas para parsers complexos. |
| Escopo excessivo para o tempo da disciplina | Alto | Restringir o MVP rigorosamente à API REST/Swagger sem frontend na primeira fase, focando no motor de lógica e regras de negócio. |
