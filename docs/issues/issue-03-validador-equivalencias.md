# [Issue 03] Validador de Equivalências Lógicas

## Contexto
Permitir que estudantes submetam duas fórmulas proposicionais e recebam a validação formal se elas são semanticamente equivalentes ($A \equiv B$), demonstrando a comparação de suas tabelas-verdade ou fornecendo uma valoração que refuta a equivalência (contraexemplo).

## Critérios de Aceite
- [x] Endpoint `POST /api/v1/equivalencia/validar` recebendo `formulaA` e `formulaB`.
- [x] O sistema deve unificar o conjunto de variáveis proposicionais presentes em ambas as fórmulas.
- [x] Retornar booleano `equivalentes: true/false`.
- [x] Em caso de não equivalência, retornar o primeiro contraexemplo de valoração (ex: `{P: true, Q: false}`) em que os resultados de $A$ e $B$ divergem.
- [x] Validação de leis clássicas (De Morgan, Dupla Negação, Implicação Material, etc.).

## Tasks Técnicas
- [x] Criar `EquivalenciaService` utilizando o motor de resolução lógica.
- [x] Implementar comparador de tabelas-verdade para o par $(A, B)$ sob as mesmas interpretações.
- [x] Criar DTOs `EquivalenciaRequestDTO` e `EquivalenciaResponseDTO`.
- [x] Criar `EquivalenciaResource` com documentação Swagger.

## Testes Exigidos
- [x] Testes unitários para leis de De Morgan: `~(P ^ Q)` vs `~P v ~Q` (deve ser equivalente).
- [x] Testes unitários para implicação material: `P -> Q` vs `~P v Q` (deve ser equivalente).
- [x] Testes unitários para casos falsos com verificação de contraexemplo gerado.

## Labels / Estimativa
`~backend` `~logica` `~equivalencia` · Estimativa: 5 pts · Prioridade: **P1** · Sprint: **Sprint 1**
