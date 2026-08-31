# [Issue 04] Módulo de Submissão e Correção Automática de Exercícios

## Contexto
Permitir que os alunos resolvam exercícios práticos de lógica formal cadastrados na base e recebam feedback instantâneo da correção, com indicação de acerto, erro e resolução passo a passo.

## Critérios de Aceite
- [ ] Endpoint `POST /api/v1/exercicios/{id}/submeter` recebendo a resposta do aluno (seja seleção de alternativa, tabela-verdade montada ou fórmula equivalente).
- [ ] O sistema valida a resposta contra o gabarito formal registrado no banco de dados.
- [ ] Retorno com status da correção (`correto: true/false`), pontuação obtida e explicação detalhada da resolução.
- [ ] Registro do histórico da tentativa (preparando o terreno para estatísticas futuras).

## Tasks Técnicas
- [ ] Criar entidade `SubmissaoExercicio` com relacionamento para `Exercicio`.
- [ ] Implementar `CorrecaoService` com estratégia de avaliação para diferentes tipos de exercícios (múltipla escolha, preenchimento de tabela e equivalência).
- [ ] Criar DTOs `SubmissaoRequestDTO` e `ResultadoCorrecaoDTO`.
- [ ] Criar endpoints no `ExercicioResource`.

## Testes Exigidos
- [ ] Testes unitários do avaliador de submissão para respostas corretas e incorretas.
- [ ] Testes de integração simulando o fluxo de listagem de exercício -> submissão -> recebimento do feedback.

## Labels / Estimativa
`~backend` `~educacional` `~correcao` · Estimativa: 5 pts · Prioridade: **P1** · Sprint: **Sprint 1**
