# [Issue 05] Infraestrutura de Persistência, Migrações Flyway e Testcontainers

## Contexto
Estruturar a fundação de banco de dados relacional (PostgreSQL) para o serviço principal Quarkus, garantindo versionamento estruturado do esquema via Flyway, documentação OpenAPI/Swagger e suíte de testes de integração com Testcontainers.

## Critérios de Aceite
- [ ] Configuração do Quarkus com `quarkus-hibernate-orm-panache`, `quarkus-jdbc-postgresql` e `quarkus-flyway`.
- [ ] Scripts SQL iniciais de migração em `src/main/resources/db/migration/` (ex: `V1.0.0__create_exercicios_tables.sql`).
- [ ] Suporte a Testcontainers para inicialização automática de instância descartável do PostgreSQL nos testes `@QuarkusTest`.
- [ ] Swagger UI e documentação OpenAPI acessíveis em `/q/swagger-ui` e `/q/openapi`.
- [ ] Health checks e readiness probes configurados via SmallRye Health (`/q/health`).

## Tasks Técnicas
- [ ] Configurar `application.properties` para perfis `dev`, `test` e `prod`.
- [ ] Escrever migrações DDL para tabelas `tb_exercicios`, `tb_submissoes` e `tb_logs_processamento`.
- [ ] Configurar extensão `quarkus-smallrye-openapi` com metadados do projeto Sílógica.
- [ ] Criar classe base de testes de integração com container PostgreSQL.

## Testes Exigidos
- [ ] Execução das migrações do Flyway durante o ciclo de testes sem falhas de DDL.
- [ ] Teste de health check garantindo que o banco de dados responde como UP.

## Labels / Estimativa
`~devops` `~database` `~flyway` · Estimativa: 5 pts · Prioridade: **P1** · Sprint: **Sprint 1**
