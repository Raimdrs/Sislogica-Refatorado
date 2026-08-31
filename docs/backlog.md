# Backlog Inicial — Sílógica

Histórias no formato padrão: **Como [papel], quero [ação] para [benefício]**.  
Convenção de Prioridade:
- **P1**: Essencial para o MVP (Sprint 1)
- **P2**: Importante para a evolução do produto (Sprint 2 / Bloco Final)
- **P3**: Otimização técnica, microsserviço e escalabilidade (Sprint 2 e Sprint 3)

---

## Tabela do Backlog

| Prio | ID | História de Usuário | Critérios de Aceitação | Estimativa | Entrega Prevista |
|---|---|---|---|---|---|
| **P1** | US01 | Como estudante, quero **inserir uma proposição lógica para gerar sua tabela-verdade automaticamente**. | • Aceita fórmulas com variáveis ($P, Q, R$) e operadores lógicos ($\land, \lor, \neg, \rightarrow, \leftrightarrow$).<br>• Calcula todas as valorações booleanas ($2^n$).<br>• Retorna matriz estruturada (passo a passo) via endpoint REST. | 8 pts | Sprint 1 |
| **P1** | US02 | Como estudante, quero **resolver listas de exercícios de lógica para testar meu aprendizado**. | • Lista exercícios filtrados por nível de dificuldade e tópicos.<br>• Endpoint de submissão valida a resposta e retorna feedback imediato (acerto/erro com justificativa). | 5 pts | Sprint 1 |
| **P1** | US03 | Como administrador, quero **cadastrar, editar e remover exercícios no sistema** para ampliar o conteúdo. | • Endpoints de CRUD completo para exercícios.<br>• Suporte a enunciados, alternativas, fórmulas proposicionais e gabaritos formais.<br>• Validação dos campos obrigatórios. | 5 pts | Sprint 1 |
| **P1** | US04 | Como estudante, quero **validar equivalências lógicas básicas** entre duas proposições para verificar se são equivalentes. | • Endpoint recebe duas fórmulas e avalia se $(P \iff Q)$ é uma tautologia.<br>• Retorna confirmação de equivalência e contraexemplo quando inválido. | 5 pts | Sprint 1 |
| **P1** | US05 | Como desenvolvedor, quero **configurar a infraestrutura básica, migrações com Flyway e testes com Testcontainers** para garantir a confiabilidade do banco PostgreSQL. | • Migrações versionadas criadas no Flyway.<br>• Testcontainers configurado para subir PostgreSQL nos testes de integração `@QuarkusTest`.<br>• OpenAPI / Swagger UI disponível. | 5 pts | Sprint 1 |
| **P3** | US06 | Como sistema, quero **processar o parser de fórmulas complexas em um microsserviço dedicado em Go** para desacoplar a computação pesada. | • Microsserviço em Go recebe a string da fórmula via gRPC.<br>• Valida a sintaxe formal (gramática proposicional) e retorna a árvore sintática estruturada (AST) ao Quarkus.<br>• Tratamento de erros de sintaxe detalhado. | 8 pts | Sprint 2 |
| **P3** | US07 | Como sistema, quero **utilizar cache nas consultas frequentes de proposições e exercícios** para reduzir o tempo de resposta e consumo de banco. | • Implementação de cache (Caffeine / Redis no Quarkus) para proposições já resolvidas e listagens populares.<br>• Exposição de métricas de acerto/erro de cache (Micrometer / Prometheus). | 5 pts | Sprint 3 |
| **P2** | US08 | Como estudante, quero **me autenticar na plataforma para salvar meu histórico de progresso**. | • Autenticação via JWT (tokens de acesso).<br>• Persistência do histórico de submissões e taxa de acerto por usuário.<br>• Proteção de rotas administrativas e de perfil. | 8 pts | Bloco Final |

---

## Resumo das Estimativas

- **Total de pontos estimados no MVP (P1 - Sprint 1):** 28 pontos
- **Total de pontos em microsserviços e cache (P3 - Sprints 2 e 3):** 13 pontos
- **Total de pontos em autenticação/histórico (P2 - Bloco Final):** 8 pontos
- **Total geral do backlog inicial:** 49 pontos
