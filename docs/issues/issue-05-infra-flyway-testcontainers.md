# [Issue 05] Infraestrutura de Persistência, Migrações Flyway e Testcontainers

## Contexto
Estruturar a fundação de banco de dados relacional (PostgreSQL) para o serviço principal Quarkus, garantindo versionamento estruturado do esquema via Flyway, documentação OpenAPI/Swagger e suíte de testes de integração com Testcontainers.

## Critérios de Aceite
- [x] Configuração do Quarkus com `quarkus-hibernate-orm-panache`, `quarkus-jdbc-postgresql`, `quarkus-flyway`, `quarkus-hibernate-validator` e `archunit-junit5`.
- [x] Scripts SQL iniciais de migração em `src/main/resources/db/migration/` (`V1__create_turmas_table.sql` e `V2__create_silogismos_table.sql`).
- [x] Configurar `application.properties` com `quarkus.hibernate-orm.database.generation=none` (esquema gerado exclusivamente via Flyway).
- [ ] Suporte a Testcontainers / Dev Services para inicialização automática do PostgreSQL nos testes `@QuarkusTest`.
- [x] Swagger UI e documentação OpenAPI acessíveis em `/q/swagger-ui` e `/q/openapi`.
- [ ] Teste de arquitetura com ArchUnit validando as fronteiras de Clean Architecture.

## Tasks Técnicas
- [x] Adicionar dependências no `pom.xml`.
- [x] Configurar `application.properties` com credenciais e estratégias de migração.
- [x] Escrever migrações DDL: `V1__create_turmas_table.sql` e `V2__create_silogismos_table.sql`.
- [ ] Criar classe de teste de arquitetura `ArchitectureTest.java` com ArchUnit.
- [ ] Validar execução das migrações do Flyway durante o ciclo de testes sem falhas de DDL.

## Testes Exigidos
- [x] Compilação do projeto com as novas dependências (`BUILD SUCCESS`).
- [ ] Execução das migrações do Flyway nos testes automatizados.
- [ ] Teste de arquitetura do ArchUnit passando no CI.

## Responsável Sugerido
- **Raí de Medeiros Cunha** (`~devops`, `~infra`, `~database`) · Estimativa: 5 pts · Prioridade: **P1** · Sprint: **Sprint 1**
