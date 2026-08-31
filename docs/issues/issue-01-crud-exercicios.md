# [Issue 01] CRUD de Exercícios e Conteúdos

## Contexto
O sistema precisa fornecer aos administradores a capacidade de cadastrar, listar, atualizar e remover exercícios e proposições de lógica formal no sistema. Este módulo compõe a base de dados para que os estudantes possam praticar.

## Critérios de Aceite
- [ ] Endpoints REST disponíveis em `/api/v1/exercicios`:
  - `POST /api/v1/exercicios`: Criação de novo exercício com enunciado, fórmula proposicional, nível de dificuldade e gabarito.
  - `GET /api/v1/exercicios`: Listagem paginada e com filtros por nível (Fácil, Médio, Difícil).
  - `GET /api/v1/exercicios/{id}`: Detalhamento de um exercício específico.
  - `PUT /api/v1/exercicios/{id}`: Atualização de dados de exercício.
  - `DELETE /api/v1/exercicios/{id}`: Remoção lógica/física de exercício.
- [ ] Validações de entrada com Hibernate Validator (Bean Validation): campos obrigatórios, tamanho de texto, formato de fórmula.
- [ ] Tratamento de exceções com retornos padronizados em JSON (RFC 7807 - Problem Details).

## Tasks Técnicas
- [ ] Criar entidade JPA `Exercicio` mapeando campos: `id`, `titulo`, `enunciado`, `formula`, `dificuldade`, `gabarito`, `criadoEm`.
- [ ] Criar `ExercicioRepository` utilizando Panache.
- [ ] Criar DTOs de Request e Response (`ExercicioRequestDTO`, `ExercicioResponseDTO`).
- [ ] Implementar `ExercicioService` com as regras de negócio.
- [ ] Criar `ExercicioResource` expondo os endpoints REST com anotações OpenAPI.

## Testes Exigidos
- [ ] Testes unitários do `ExercicioService` utilizando Mockito / QuarkusMock.
- [ ] Testes de integração dos endpoints com `@QuarkusTest` e REST-assured validando status 200, 201, 400 e 404.

## Labels / Estimativa
`~backend` `~quarkus` `~crud` · Estimativa: 5 pts · Prioridade: **P1** · Sprint: **Sprint 1**
