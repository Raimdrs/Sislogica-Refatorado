# Sílógica — Plataforma Educacional de Lógica Formal

[![CI Pipeline](https://github.com/Raimdrs/Sislogica-Refatorado/actions/workflows/ci.yml/badge.svg)](https://github.com/Raimdrs/Sislogica-Refatorado/actions/workflows/ci.yml)

> Projeto desenvolvido para a disciplina **DIM0547 — Desenvolvimento de Sistemas Web II (2026.2)**  
> **Coorte:** Coorte B  
> **Vídeo de Apresentação (Sprint 0):** *(Link do vídeo de 5 minutos)*

---

## 👥 Equipe e Responsabilidades

| Integrante | Matrícula | Papel Principal |
|---|---|---|
| **Samuel Ótton Nogueira Maia** | 20210055968 | Desenvolvedor Backend / Arquitetura |
| **Raí de Medeiros Cunha** | 20260072994 | Desenvolvedor Backend / DevOps |
| **Moab Fred dos Santos Varela** | 20260072967 | Desenvolvedor Backend / QA |

---

## 🏛️ Estrutura do Monorepo

O projeto está organizado como um monorepo modular:

```text
.
├── .github/
│   └── workflows/
│       └── ci.yml             # Pipeline de CI (Java/Quarkus + Go)
├── api/                       # Serviço Principal (Java 21 + Quarkus + REST)
│   ├── src/
│   ├── pom.xml
│   └── mvnw / mvnw.cmd
├── services/
│   └── parser/                # Microsserviço de Alta Performance (Go + AST)
│       ├── main.go
│       ├── main_test.go
│       └── go.mod
├── protos/                    # Contratos de Comunicação gRPC (Protobuf v3)
│   └── parser.proto
├── docs/                      # Documentação Técnica e de Processo
│   ├── proposta.md            # Proposta e Decisões de Arquitetura da Sprint 0
│   ├── backlog.md             # Backlog Inicial com Histórias de Usuário
│   ├── issues/                # Especificação detalhada de cada issue/história
│   └── sprints/               # Planejamento detalhado por sprint
├── docker-compose.yml         # Serviços auxiliares (PostgreSQL 16)
├── mise.toml                  # Automação de tarefas de build/test
└── README.md
```

---

## 🛠️ Tecnologias e Arquitetura

- **Serviço Principal:** [Java 21](https://adoptium.net/) & [Quarkus 3.x](https://quarkus.io/) (Framework Cloud-Native de alta performance, REST, OpenAPI/Swagger).
- **Microsserviço Especializado:** [Go 1.22+](https://go.dev/) (Parser formal de fórmulas proposicionais, análise sintática e geração de AST).
- **Comunicação Inter-serviços:** [gRPC / Protocol Buffers (proto3)](https://grpc.io/).
- **Banco de Dados:** [PostgreSQL 16](https://www.postgresql.org/).
- **Integração Contínua (CI):** [GitHub Actions](https://github.com/features/actions).

---

## 🚀 Como Rodar o Projeto Localmente

### 1. Pré-requisitos
- **Java 21** (JDK 21+)
- **Go 1.22+** (para o microsserviço de parser)
- **Docker & Docker Compose** (para infraestrutura local)
- *(Opcional)* [Mise](https://mise.jdx.dev/) para executar as tasks automatizadas

---

### 2. Subir a Infraestrutura (Banco de Dados)
```bash
docker compose up -d postgres
```

---

### 3. Rodar os Serviços

#### A. Serviço Principal (Java / Quarkus)
```bash
cd api
# No Linux / macOS:
./mvnw quarkus:dev

# No Windows:
./mvnw.cmd quarkus:dev
```
- A API estará disponível em: `http://localhost:8080`
- Swagger UI / Documentação OpenAPI: `http://localhost:8080/q/swagger-ui/`
- Healthcheck: `http://localhost:8080/health`

#### B. Microsserviço de Parser (Go)
```bash
cd services/parser
go run main.go
```

---

### 4. Executar Testes e Builds

#### Via `mise`:
```bash
mise run build   # Compila ambos os serviços
mise run test    # Executa os testes de ambos os serviços
```

#### Manualmente:
```bash
# Testes do Quarkus:
cd api && ./mvnw test

# Testes do Go:
cd services/parser && go test -v ./...
```

---

## 📖 Documentação da Proposta e Backlog

- 📄 [Proposta do Projeto & Decisões Arquiteturais](docs/proposta.md)
- 📋 [Backlog Inicial e Histórias de Usuário](docs/backlog.md)
- 📌 [Quadro no GitHub Projects](https://github.com/users/Raimdrs/projects) *(Configuração no GitHub)*
