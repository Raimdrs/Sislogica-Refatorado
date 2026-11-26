# ADR 001 — Responsabilidade e Isolamento do Microsserviço de Parser Lógico em Go

**Data:** 24/11/2026  
**Status:** Aceita  
**Contexto da Sprint:** Sprint 2 — DIM0547 (T2 / Issue 06)

---

## 1. Contexto

O **Sílógica** possui uma API principal em Java 21 + Quarkus responsável pelos fluxos transacionais e educacionais (gestão de turmas, silogismos aristotélicos, persistência relacional no PostgreSQL com Flyway, cache Caffeine e métricas Prometheus).

Além das operações CRUD, o domínio exige operações formais de **lógica proposicional**:
1. Análise léxica e sintática de fórmulas proposicionais com geração de Árvore Sintática Abstrata (**AST**);
2. Unificação de variáveis e avaliação combinatória de equivalência lógica ($A \equiv B$) por tabela-verdade ($2^n$ combinações) com identificação de contraexemplo.

Esse tipo de carga é **CPU-bound** e de crescimento exponencial ($O(2^n)$ em relação ao número de variáveis proposicionais), com perfil computacional radicalmente diferente das rotas transacionais de I/O de banco da API REST.

---

## 2. Decisão

Manter um **microsserviço dedicado em Go** (`services/parser/`), comunicado com a API Quarkus via **gRPC / Protocol Buffers v3** (`protos/sislogica/parser/v1/parser.proto`), com a seguinte responsabilidade única em uma frase:

> **Executar a análise léxica/sintática formal (geração de AST) e a avaliação combinatória de equivalência lógica por tabela-verdade ($2^n$) de fórmulas proposicionais via gRPC, de forma stateless e sem compartilhar banco de dados com a API principal.**

### Princípios adotados
- **Sem compartilhamento de tabelas:** o serviço Go é 100% *stateless* (recebe fórmulas, processa em memória e devolve AST / tabela-verdade / contraexemplo); ele nunca acessa o PostgreSQL da API Quarkus.
- **Cancelamento e prazo nativos (`context.Context`):** toda operação de parsing e avaliação recebe `ctx context.Context` como primeiro parâmetro e verifica `ctx.Done()` durante a execução, interrompendo imediatamente cálculos cujo prazo expirou.
- **Domínio puro desacoplado de rede:** o pacote `internal/domain` não importa gRPC nem HTTP, expõe erros nomeados (`errors.Is` + `%w`) e é testado isoladamente com `go test ./...`.
- **Contrato versionado (`v1`):** definido em `protos/sislogica/parser/v1/parser.proto`, validado por `buf lint` (`STANDARD` + `COMMENTS`) e com stubs gerados automaticamente (`buf generate` para Go e `quarkus-grpc` para Java).

---

## 3. Por que fica fora da API — e o que aconteceria se ficasse dentro

- **Por que fica fora da API:**
  - Isola o processamento intensivo de CPU ($O(2^n)$) do processo da API REST, permitindo escalar ou reiniciar o motor lógico de forma independente.
  - Go entrega compilação nativa com baixa pegada de memória (imagem final mínima em container), ausência de pausas longas de *garbage collector* sob alta alocação de nós AST e cancelamento cooperativo barato via `context.Context`.
  - Contratos binários via gRPC/Protobuf reduzem o custo de serialização da tabela-verdade e garantem tipagem forte entre as duas linguagens.
- **O que aconteceria se ficasse exclusivamente dentro da API Quarkus:**
  - Múltiplas avaliações simultâneas de fórmulas com muitas variáveis (ex.: 10–12 variáveis, $1024$ a $4096$ linhas por requisição) competiriam por CPU, memória heap e threads de trabalho da JVM com as operações CRUD de Turmas e Silogismos, aumentando a latência (p95/p99) e podendo causar contenção ou esgotamento do pool de threads da API.
  - Não seria possível escalar o motor computacional independentemente da camada transacional REST/PostgreSQL.

---

## 4. Alternativas Consideradas

| Alternativa | Prós | Contras / Motivo do descarte |
|---|---|---|
| **Manter tudo dentro da API Quarkus (monólito)** | Menor complexidade operacional e zero salto de rede | Mistura carga CPU-bound ($2^n$) com CRUD transacional no mesmo processo JVM; não permite escalar o motor lógico separadamente |
| **Comunicação via REST/JSON entre Quarkus e Go** | Fácil depuração manual com `curl` | Maior overhead de serialização de tabelas-verdade e ausência de contrato versionado fortemente tipado com geração automática de stubs e *deadlines* nativos |
| **Fila assíncrona (RabbitMQ / Kafka)** | Bom para jobs longos em segundo plano | Inadequado para feedback interativo instantâneo em tempo real na API educacional; adiciona infraestrutura desnecessária |
| **Separar um CRUD (ex.: Silogismos) em Go dividindo o PostgreSQL** | — | **Antipadrão:** acopla os dois serviços pelo mesmo esquema de banco, impedindo evolução independente |

---

## 5. Consequências

### Positivas
- Separação clara de responsabilidades (Quarkus = domínio transacional/persistência/REST; Go = motor computacional *stateless* de lógica formal).
- Contrato único e auditável em `protos/` validado automaticamente no CI via `buf lint`.
- Encerramento gracioso (`GracefulStop`), Health Check gRPC (`SERVING`) e Reflection disponíveis nativamente.

### Negativas e Mitigações
- **Custo de rede e possibilidade de indisponibilidade do serviço Go:** mitigado no cliente Quarkus (T6) exigindo **prazo obrigatório (`withDeadlineAfter`)** em todas as chamadas e **degradação graciosa** — caso o serviço Go esteja inacessível (`UNAVAILABLE` ou `DEADLINE_EXCEEDED`), a API continua respondendo dentro do prazo utilizando avaliação local de fallback.
- **Limitação de gRPC entre serviços em planos gratuitos na nuvem:** no ambiente de produção gratuito (T10), a API pode operar de forma autônoma com degradação graciosa, enquanto a integração completa API ↔ Go é executada e validada localmente via `docker compose` e nos testes automatizados do CI.
