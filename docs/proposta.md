# Proposta do Projeto — Sprint 0: Sílógica

**Disciplina:** DIM0547 - Desenvolvimento de Sistemas Web II (2026.2)  
**Coorte:** Coorte B  

---

## 1. Visão do Produto

```text
Para estudantes de computação, filosofia e candidatos a concursos que precisam dominar lógica formal
Que enfrentam dificuldades para praticar e validar argumentos, proposições e tabelas-verdade de forma interativa e com feedback imediato
O Sílógica é uma plataforma web educacional de apoio ao aprendizado de lógica formal
Que fornece geradores de tabelas-verdade, analisadores de validade de argumentos e resolvedores de exercícios guiados
Diferente de listas de exercícios estáticas em PDF ou softwares acadêmicos antiquados e difíceis de instalar
Nosso produto oferece validação algorítmica instantânea de estruturas proposicionais (como P, Q e operadores ∧, ∨, ¬, →) em uma interface moderna e acessível via web.
```

**Hipótese de valor:** Acreditamos que estudantes de lógica vão utilizar a plataforma regularmente porque o feedback automatizado e instantâneo em tabelas-verdade e proposições lógicas aceleram a fixação do conteúdo comparado ao estudo puramente manual.

---

## 2. Definição do MVP (Mínimo Produto Viável)

O MVP foca em disponibilizar o núcleo algorítmico e as regras de negócio de lógica proposicional via API REST robusta e documentada.

| No MVP | Fora do MVP |
|---|---|
| CRUD completo das entidades principais e regras de negócio via API REST | Gerador de argumentos complexos via Inteligência Artificial |
| Módulo interativo de construção e resolução de **Tabelas-Verdade** para proposições compostas | **Frontend / Interface Web**: Validação feita via API / Swagger / Postman / cURL |
| Validador de equivalências lógicas básicas e operadores ($\land, \lor, \neg, \rightarrow$) | Relatórios gerenciais complexos |
| Persistência de dados relacional e migrações estruturadas (PostgreSQL + Flyway) | Autenticação e Perfis: login, sessões e tokens JWT/OAuth2 (fora do MVP inicial) |
| Módulo de exercícios práticos com correção automática | Fórum de dúvidas integrado entre alunos |

---

## 3. Link do Backlog e Histórias de Usuário

- **Quadro no GitHub Projects:** [Quadro de Tarefas do Sílógica](https://github.com/users/Raimdrs/projects)
- **Documento de Backlog detalhado:** [`docs/backlog.md`](backlog.md)
- **Especificações de Issues:** [`docs/issues/`](issues/)

O backlog inicial contém 8 histórias priorizadas (P1, P2 e P3), com estimativas em story points e critérios de aceite definidos.

---

## 4. Entidades Principais do Domínio

1. **Usuário / Administrador:** Responsável pelo gerenciamento das permissões, auditoria e cadastro de conteúdos.
2. **Fonte / Endpoint:** Origem externa dos dados monitorados e integrados com o microsserviço Go.
3. **Regra / Registro:** Lógica de negócio e regras de inferência aplicadas sobre os dados e proposições coletadas.
4. **Log de Processamento:** Histórico de execuções, avaliações, falhas e sucessos das validações e auditorias.
5. **Exercício / Proposição:** Modelagem formal das fórmulas, operadores lógicos envolvidos, gabaritos e resolução passo a passo.

---

## 5. A Escolha Tecnológica: Java / Quarkus

Optamos por **Java com Quarkus** como stack do serviço principal com base nos seguintes pilares:

- **Perfil da equipe:** A equipe possui sólida afinidade com a linguagem Java e deseja consolidar os fundamentos da JVM utilizando um framework moderno de alta performance.
- **Características do domínio:** O Quarkus é otimizado para containers (GraalVM / OpenJDK), oferecendo um tempo de partida extremamente baixo (*fast boot*) e consumo reduzido de memória (*low memory footprint*), o que facilita o deploy e a escalabilidade na nuvem.
- **Mercado e ecossistema:** Java possui altíssima empregabilidade e um ecossistema maduro. O Quarkus traz suporte nativo e produtivo para padrões corporativos, integração com OpenAPI/Swagger, migrações com Flyway e testes robustos de integração via Testcontainers.
- **Diferenciação didática:** Explorar o Quarkus permite aplicar conceitos modernos de microsserviços nativos para a nuvem (*Cloud Native*) com injeção de dependências em tempo de compilação (CDI / ArC).

---

## 6. Divisão de Responsabilidades (Serviço Principal vs. Go)

| Vai para o serviço principal (Java / Quarkus) | Vai para um microsserviço Go |
|---|---|
| Modelagem das entidades principais do sistema | Parser formal de fórmulas e proposições lógicas complexas |
| Endpoints REST para gerenciamento de exercícios, listagem de conteúdos e submissão de respostas | Validação sintática formal e geração de árvore sintática (AST) |
| Persistência relacional com PostgreSQL e migrações Flyway | Comunicação RPC de alta velocidade com o Quarkus via **gRPC** |
| Implementação de camada de cache para consultas frequentes | Processamento assíncrono e intensivo de CPU para análise lógica |
| Coordenação dos fluxos da aplicação web educacional | — |

**Justificativa:**  
A linguagem Go possui excelente desempenho e concorrência nativa para tarefas intensivas de parsing e compilação/análise léxica de árvores sintáticas. O serviço em Java/Quarkus centraliza a lógica de domínio, a persistência transacional e a interface REST para clientes externos, comunicando-se com o microsserviço Go via gRPC para tarefas de validação sintática complexa.

---

## 7. Equipe e Papéis

| Nome | Matrícula | Papel / Responsabilidade |
|---|---|---|
| Samuel Ótton Nogueira Maia | 20210055968 | Desenvolvedor Backend / Arquitetura |
| Raí de Medeiros Cunha | 20260072994 | Desenvolvedor Backend / DevOps |
| Moab Fred dos Santos Varela | 20260072967 | Desenvolvedor Backend / QA |

---

## 8. Coorte e Apresentação

- **Coorte:** Coorte B  
- **Disciplina:** DIM0547 - Desenvolvimento de Sistemas Web II (2026.2)  
- **Vídeo de Apresentação (Sprint 0):** *https://drive.google.com/file/d/1NDhcFxtrJrSfdpQKHr82sNbHFNqZ2etL/view?usp=sharing*
