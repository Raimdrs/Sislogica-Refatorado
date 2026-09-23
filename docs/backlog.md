# Backlog Inicial — Sílógica

Histórias no formato padrão: **Como [papel], quero [ação] para [benefício]**.  
Convenção de Prioridade:
- **P1**: Essencial para o MVP (Sprint 1)
- **P2**: Importante para a evolução do produto (Sprint 2 / Bloco Final)
- **P3**: Otimização técnica, microsserviço e escalabilidade (Sprint 2 e Sprint 3)

---

## Tabela do Backlog

| Prio | ID | História de Usuário | Critérios de Aceitação | Estimativa | Entrega Prevista | Responsável |
|---|---|---|---|---|---|---|
| **P1** | US01 | Como professor, quero **cadastrar, listar e gerenciar turmas/classes de lógica** no sistema. | • CRUD completo de turmas via REST (`201 Location`, `204`, `404`).<br>• Paginação e filtro por professor.<br>• Bean Validation e Problem Details. | 5 pts | Sprint 1 | Samuel Ótton |
| **P1** | US02 | Como estudante/professor, quero **cadastrar e consultar silogismos aristotélicos vinculados a uma turma**. | • CRUD de silogismos com relacionamento 1:N com Turma.<br>• Rota aninhada `/api/v1/turmas/{id}/silogismos`.<br>• Paginação real no SQL via Panache e filtro por modo. | 5 pts | Sprint 1 | Moab Fred |
| **P1** | US05 | Como desenvolvedor, quero **estruturar a persistência Flyway, testes com Dev Services e verificação Clean Architecture com ArchUnit**. | • Migrações versionadas Flyway (`V1` e `V2`).<br>• Teste de arquitetura com ArchUnit rodando no CI.<br>• Suíte de testes automatizados passando localmente e no GitHub Actions. | 5 pts | Sprint 1 | Raí de Medeiros |
| **P1** | US03 | Como estudante, quero **validar a consistência e regras formais de um silogismo**. | • Validador de regras aristotélicas de silogismos (ex: de duas negativas nada se conclui).<br>• Retorna feedback estruturado e identificação de falácias. | 5 pts | Sprint 2 | Equipe |
| **P1** | US04 | Como estudante, quero **gerar tabelas-verdade e validar equivalências lógicas básicas**. | • Endpoint avalia proposições booleanas ($P \land Q, P \lor Q$).<br>• Retorna matriz de valorações verdade. | 5 pts | Sprint 2 | Equipe |
| **P3** | US06 | Como sistema, quero **processar o parser formal de fórmulas lógicas em microsserviço dedicado em Go**. | • Microsserviço em Go recebe a expressão via gRPC.<br>• Gera a árvore sintática (AST) com alta performance. | 8 pts | Sprint 2 | Equipe |
| **P3** | US07 | Como sistema, quero **utilizar cache nas consultas frequentes** para otimizar tempo de resposta. | • Cache de silogismos e turmas com Caffeine / Redis.<br>• Métricas Micrometer / Prometheus. | 5 pts | Sprint 3 | Equipe |
| **P2** | US08 | Como usuário, quero **autenticação com JWT para salvar meu progresso**. | • Autenticação e controle de acesso via tokens JWT. | 8 pts | Bloco Final | Equipe |

---

## Resumo das Estimativas

- **Total de pontos estimados no MVP (P1 - Sprint 1):** 28 pontos
- **Total de pontos em microsserviços e cache (P3 - Sprints 2 e 3):** 13 pontos
- **Total de pontos em autenticação/histórico (P2 - Bloco Final):** 8 pontos
- **Total geral do backlog inicial:** 49 pontos
