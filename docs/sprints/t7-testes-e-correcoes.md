# [T7] Correção de Geração de IDs, Limpeza Arquitetural e Suíte Completa de Testes

Documento de resumo técnico para facilitar a revisão do Pull Request e o alinhamento entre a equipe (**Moab, Raí e Samuel**).

---

## 1. Problema Crítico Encontrado e Corrigido (`POST` retornando HTTP 500)

### O que estava acontecendo
Ao testar requisições `POST /api/v1/turmas` e `POST /api/v1/silogismos` contra um PostgreSQL real (com as migrações Flyway `V1`–`V4`), o Hibernate lançava erro **500 Internal Server Error**:

```text
ERROR: relation "silogismos_seq" does not exist
[select nextval('silogismos_SEQ')]
```

### Por que acontecia
- As migrações Flyway (`V1` a `V4`) criam todas as chaves primárias com `id BIGSERIAL PRIMARY KEY` (estratégia `IDENTITY` do PostgreSQL).
- As 5 entidades JPA estendiam `PanacheEntity`, que no Hibernate 6 / Quarkus 3 usa `@GeneratedValue` (`AUTO`) e espera sequências `<tabela>_SEQ` (que não existem no schema gerenciado exclusivamente pelo Flyway).
- Esse erro não tinha aparecido antes porque os testes `@QuarkusTest` anteriores só executavam `GET` vazio, `404` e `400` de payload vazio — nunca um `POST` válido no PostgreSQL.

### Como foi corrigido
Nas 5 entidades em `api/src/main/java/com/sislogica/api/infrastructure/entity/`:
- `TurmaPanacheEntity`
- `SilogismoPanacheEntity`
- `ProposicaoPanacheEntity`
- `SilogismoDetalhePanacheEntity`
- `ReducaoPanacheEntity`

Substituímos `extends PanacheEntity` por `extends PanacheEntityBase` com mapeamento explícito alinhado ao `BIGSERIAL`:

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
public Long id;
```

**Vantagens:** não altera nenhuma migração Flyway já aplicada (`V1`–`V4`), mantém todos os métodos estáticos do Panache (`persist`, `find`, `findById`, `deleteById`) e garante compatibilidade total com PostgreSQL local, Dev Services (Testcontainers/CI) e Neon.

---

## 2. Validações e Tratamento de Erros Consistentes

- **`SilogismoService`:** passou a injetar `TurmaRepositoryPort` para verificar se a `turmaId` informada existe antes de criar ou atualizar um silogismo. Se não existir, lança `IllegalArgumentException`, que o `IllegalArgumentExceptionMapper` converte automaticamente em **HTTP 400 (`application/problem+json`)** em vez de deixar estourar erro 500 de violação de chave estrangeira no banco.
  - *Compatibilidade:* o construtor anterior `SilogismoService(SilogismoRepositoryPort)` foi preservado e um novo construtor `SilogismoService(SilogismoRepositoryPort, TurmaRepositoryPort)` foi adicionado.
- **`SilogismoResource`:** adicionadas anotações `@PositiveOrZero`, `@Min(1)` e `@Positive` nos parâmetros de paginação e ID (mantendo o mesmo padrão já adotado em `TurmaResource`).

---

## 3. Clean Architecture & ArchUnit

- **DTOs desacoplados da infraestrutura:** removidos os métodos `fromEntity(...PanacheEntity)` (e imports de `infrastructure.entity.*`) que não eram utilizados em nenhum lugar do código em:
  - `ProposicaoDTO`
  - `ReducaoDTO`
  - `SilogismoDetalheDTO`
  - `SilogismoResponseDTO`
  - Todo o mapeamento continua passando exclusivamente pelo domínio via `fromDomain(...)`.
- **`ArchitectureTest` reforçado (4 regras ativas):**
  1. `dominio_nao_deve_depender_de_frameworks`: agora também bloqueia `io.grpc..` e `com.google.protobuf..` (já preparando o terreno para a T6).
  2. `dominio_nao_deve_depender_de_camadas_externas`: bloqueia `..infrastructure..`, `..application..`, `..controller..`, `..service..` e `..dto..`.
  3. `servicos_nao_devem_depender_de_infraestrutura`: garante que `..service..` dependa apenas das portas do domínio.
  4. `dtos_nao_devem_depender_de_infraestrutura`: garante que `..dto..` nunca volte a importar entidades Panache/JPA.

### Validação proposital do `ArchitectureTest` (prova contra falso positivo)
Conforme exigido na T7, foi introduzida temporariamente a anotação proibida `@jakarta.persistence.Entity` na entidade pura `com.sislogica.api.domain.model.Turma` e executado `./mvnw test -Dtest=ArchitectureTest`. O teste falhou imediatamente (`Tests run: 4, Failures: 1`):

```text
Architecture Violation [Priority: MEDIUM] - Rule 'no classes that reside in a package '..domain..' should depend on classes that reside in any package ['io.quarkus..', 'jakarta.persistence..', 'jakarta.ws.rs..', 'org.hibernate..', 'java.sql..', 'javax.sql..', 'io.grpc..', 'com.google.protobuf..'], because O domínio deve ser 100% puro e agnóstico a frameworks, banco de dados e gRPC.' was violated (1 times):
Class <com.sislogica.api.domain.model.Turma> is annotated with <jakarta.persistence.Entity> in (Turma.java:0)
```

Após a remoção da anotação temporária, o `ArchitectureTest` voltou a passar **4/4** (`BUILD SUCCESS`), confirmando que as regras estão efetivamente ativas.

---

## 4. Suíte Completa de Testes (T7 — 67 testes passando)

Todos os 3 pilares exigidos na **T7** estão cobertos e verdes (`Tests run: 67, Failures: 0, Errors: 0, Skipped: 0`):

| Tipo de Teste | Classes | O que valida |
|---|---|---|
| **Unitários de Domínio e Serviço** *(sem banco)* | `TurmaTest`, `SilogismoTest`, `ProposicaoTest`, `SilogismoDetalheTest`, `TurmaServiceTest`, `SilogismoServiceTest`, `TurmaResourceUnitTest`, `SilogismoResourceUnitTest` | Regras de negócio, invariantes de domínio (`SilogismoDetalhe`, `Proposicao`, `Reducao`), validações de turma inexistente/código duplicado com repositórios falsos |
| **Integração com PostgreSQL real (`@QuarkusTest`)** | `HealthResourceTest`, `TurmaResourceTest` (6 testes), `SilogismoResourceTest` (7 testes) | Migrações Flyway (`V1`–`V4`), CRUD completo (`POST 201 + Location`, `GET 200`, `PUT 200`, `DELETE 204`, `GET 404`), relacionamento 1:N (`GET /api/v1/turmas/{turmaId}/silogismos`), paginação real no SQL, filtro por professor e respostas de erro em `ProblemDetails` (`application/problem+json`). Os testes usam códigos únicos e limpam seus próprios registros ao final |
| **Arquitetura (ArchUnit)** | `ArchitectureTest` (4 regras) | Isolamento total do domínio (sem framework, banco ou gRPC) e fronteiras limpas entre `service`, `dto` e `infrastructure` |

---

## 5. Como Rodar os Testes

### Ambiente padrão (com Docker / Quarkus Dev Services — mesmo comando do CI)
```bash
cd api
./mvnw test
```

### Sem Docker (usando PostgreSQL local já rodando na máquina, ex.: porta 5433)
```bash
cd api
./mvnw test \
  -Dquarkus.datasource.devservices.enabled=false \
  -Dquarkus.datasource.jdbc.url=jdbc:postgresql://127.0.0.1:5433/sislogica_db \
  -Dquarkus.datasource.username=sislogica_user \
  -Dquarkus.datasource.password=sislogica_password
```

### Apenas testes unitários + ArchUnit (sem precisar de banco)
```bash
cd api
./mvnw test -Dtest='!HealthResourceTest,!SilogismoResourceTest,!TurmaResourceTest'
```
