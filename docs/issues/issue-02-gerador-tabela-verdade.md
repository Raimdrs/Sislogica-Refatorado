# [Issue 02] CRUD de Silogismos e Rota Aninhada de Turmas

## Contexto
Refatoração da entidade `PREMISSAS` do projeto original Sílógica (Python/Django). O sistema deve fornecer aos estudantes e professores a capacidade de cadastrar, listar, atualizar e remover silogismos lógicos aristotélicos vinculados a uma turma/classe (relacionamento 1:N com chave estrangeira).

## Critérios de Aceite
- [x] Endpoints REST disponíveis em `/api/v1/silogismos`:
  - `POST /api/v1/silogismos`: Cadastro de silogismo com `titulo`, `premissaMaior`, `premissaMenor`, `conclusao`, `modo` e `turmaId`. Retorna status `201 Created` e cabeçalho `Location: /api/v1/silogismos/{id}`.
  - `GET /api/v1/silogismos`: Listagem paginada no SQL via Panache (`?pagina=0&tamanho=10`) com filtro por modo (ex: `?modo=BARBARA`).
  - [x] `GET /api/v1/turmas/{turmaId}/silogismos`: **Rota aninhada** listando todos os silogismos de uma determinada turma (comprovando o relacionamento 1:N na API).
  - `GET /api/v1/silogismos/{id}`: Detalhamento de um silogismo (retorna `404 Not Found` se não existir).
  - `PUT /api/v1/silogismos/{id}`: Atualização de premissas e conclusão (retorna `404 Not Found` se não existir).
  - `DELETE /api/v1/silogismos/{id}`: Remoção de silogismo (retorna `204 No Content`).
- [x] Validações de entrada com Bean Validation (`@NotBlank`, `@NotNull`): premissas obrigatórias e modo válido.
- [x] Paginação real no SQL (`Page.of(pagina, tamanho)` no Panache, sem carregar tudo na memória).
- [x] Tratamento de exceções com retornos padronizados em JSON no padrão Problem Details (RFC 9457).

## Tasks Técnicas
- [x] Criar entidade pura de domínio `Silogismo.java` em `domain/model/`.
- [x] Criar porta de repositório `SilogismoRepositoryPort.java` em `domain/repository/`.
- [x] Criar entidade Panache `SilogismoPanacheEntity.java` mapeando a tabela `silogismos` (Flyway V2 com FK) na camada de infraestrutura.
- [x] Criar adaptador de repositório `SilogismoRepositoryAdapter.java` implementando `SilogismoRepositoryPort`.
- [x] Criar DTOs de Request e Response (`SilogismoRequestDTO`, `SilogismoResponseDTO`).
- [x] Implementar serviço de aplicação `SilogismoService.java` com consulta paginada por turma e CRUD completo.
- [x] Criar recurso REST `SilogismoResource.java` com paginação SQL no Panache, rota aninhada e anotações OpenAPI.

## Testes Exigidos
- [x] Testes unitários do modelo, serviço e recurso de silogismo (`SilogismoTest`, `SilogismoServiceTest` e `SilogismoResourceUnitTest`).
- [x] Testes de integração dos endpoints com `@QuarkusTest` e REST-assured validando status 200, 400, 404 e rota aninhada (`SilogismoResourceTest`).

## Responsável Sugerido
- **Moab Fred dos Santos Varela** (`~backend`, `~qa`, `~crud`, `~silogismos`) · Estimativa: 5 pts · Prioridade: **P1** · Sprint: **Sprint 1**
