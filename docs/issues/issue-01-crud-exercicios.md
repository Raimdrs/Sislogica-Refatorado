# [Issue 01] CRUD de Turmas (Classes de Lógica)

## Contexto
Refatoração da entidade `CLASSE` do projeto original Sílógica (Python/Django). O sistema deve fornecer aos professores a capacidade de cadastrar, listar, atualizar e remover turmas/classes no sistema. Esta entidade representa o lado "1" do relacionamento 1:N com Silogismos.

## Critérios de Aceite
- [ ] Endpoints REST disponíveis em `/api/v1/turmas`:
  - `POST /api/v1/turmas`: Criação de nova turma com `codigo` (código de acesso único), `professor` e `descricao`. Retorna status `201 Created` e cabeçalho `Location: /api/v1/turmas/{id}`.
  - `GET /api/v1/turmas`: Listagem com paginação no banco (`?pagina=0&tamanho=10`) e filtro por nome do professor.
  - `GET /api/v1/turmas/{id}`: Detalhamento de uma turma específica (retorna `404 Not Found` se não existir).
  - `PUT /api/v1/turmas/{id}`: Atualização de dados da turma (retorna `404 Not Found` se não existir).
  - `DELETE /api/v1/turmas/{id}`: Remoção de turma (retorna `204 No Content`).
- [ ] Validações de entrada com Bean Validation (`@NotBlank`, `@Size`): campos obrigatórios e tamanhos máximos.
- [ ] Tratamento de exceções com retornos padronizados em JSON no padrão Problem Details (RFC 9457 / `application/problem+json`).
- [ ] Documentação OpenAPI/Swagger gerada em `/q/swagger-ui`.

## Tasks Técnicas
- [x] Criar entidade pura de domínio `Turma.java` em `domain/model/`.
- [x] Criar porta de repositório `TurmaRepositoryPort.java` em `domain/repository/`.
- [ ] Criar entidade Panache `TurmaPanacheEntity.java` mapeando a tabela `turmas` (Flyway V1).
- [ ] Criar adaptador de repositório `TurmaRepositoryAdapter.java` implementando `TurmaRepositoryPort`.
- [ ] Criar DTOs de Request e Response (`TurmaRequestDTO`, `TurmaResponseDTO`).
- [ ] Implementar serviço de aplicação `TurmaService.java` com as regras de negócio.
- [ ] Criar recurso REST `TurmaResource.java` com paginação via Panache e anotações OpenAPI.

## Testes Exigidos
- [ ] Testes unitários do `TurmaService` validando as regras de negócio e validações.
- [ ] Testes de integração dos endpoints com `@QuarkusTest` e REST-assured validando status 200, 201 com Location, 400 e 404.

## Responsável Sugerido
- **Samuel Ótton Nogueira Maia** (`~backend`, `~quarkus`, `~crud`, `~turmas`) · Estimativa: 5 pts · Prioridade: **P1** · Sprint: **Sprint 1**
