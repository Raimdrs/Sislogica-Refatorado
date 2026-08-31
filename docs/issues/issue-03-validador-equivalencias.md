# [Issue 03] Validador de Equivalências Lógicas

## Contexto
Permitir que estudantes submetam duas fórmulas proposicionais e recebam a validação formal se elas são semanticamente equivalentes ($A \equiv B$), demonstrando a comparação de suas tabelas-verdade ou fornecendo uma valoração que refuta a equivalência (contraexemplo).

## Critérios de Aceite
- [ ] Endpoint `POST /api/v1/equivalencia/validar` recebendo `formulaA` e `formulaB`.
- [ ] O sistema deve unificar o conjunto de variáveis proposicionais presentes em ambas as fórmulas.
- [ ] Retornar booleano `equivalentes: true/false`.
- [ ] Em caso de não equivalência, retornar o primeiro contraexemplo de valoração (ex: `{P: true, Q: false}`) em que os resultados de $A$ e $B$ divergem.
- [ ] Validação de leis clássicas (De Morgan, Dupla Negação, Implicação Material, etc.).

## Tasks Técnicas
- [ ] Criar `EquivalenciaService` utilizando o motor de resolução lógica.
- [ ] Implementar comparador de tabelas-verdade para o par $(A, B)$ sob as mesmas interpretações.
- [ ] Criar DTOs `EquivalenciaRequestDTO` e `EquivalenciaResponseDTO`.
- [ ] Criar `EquivalenciaResource` com documentação Swagger.

## Testes Exigidos
- [ ] Testes unitários para leis de De Morgan: `~(P ^ Q)` vs `~P v ~Q` (deve ser equivalente).
- [ ] Testes unitários para implicação material: `P -> Q` vs `~P v Q` (deve ser equivalente).
- [ ] Testes unitários para casos falsos com verificação de contraexemplo gerado.

## Labels / Estimativa
`~backend` `~logica` `~equivalencia` · Estimativa: 5 pts · Prioridade: **P1** · Sprint: **Sprint 1**
