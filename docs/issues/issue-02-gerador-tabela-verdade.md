# [Issue 02] Módulo de Geração e Resolução de Tabelas-Verdade

## Contexto
O diferencial pedagógico do Sílógica é a capacidade de gerar automaticamente a Tabela-Verdade completa e passo a passo a partir de uma proposição composta informada pelo usuário, exibindo todas as colunas intermediárias das subfórmulas.

## Critérios de Aceite
- [ ] Endpoint `POST /api/v1/tabela-verdade/gerar` que recebe:
  - Uma string com a expressão lógica proposicional (ex: `(P ^ Q) -> (~P v R)`).
- [ ] Reconhecimento de variáveis proposicionais ($P, Q, R, S$, etc.) e operadores:
  - Negação: `~`, `not`, `¬`
  - Conjunção: `^`, `and`, `∧`
  - Disjunção: `v`, `or`, `∨`
  - Condicional (Implicação): `->`, `implies`, `→`
  - Bicondicional (Equivalência): `<->`, `iff`, `↔`
- [ ] Geração das $2^n$ linhas com combinações booleanas (True/False ou V/F).
- [ ] Decomposição em subfórmulas com cabeçalhos de colunas intermediárias e resultado final.
- [ ] Identificação da classificação da fórmula: Tautologia, Contradição ou Contingência.

## Tasks Técnicas
- [ ] Implementar motor de avaliação de expressões lógicas proposicionais (Shunting-yard ou AST).
- [ ] Criar gerador combinatório de valorações para $N$ variáveis booleanas.
- [ ] Criar classe `TabelaVerdadeResultDTO` contendo `variaveis`, `subformulas`, `linhas` (matriz de valores) e `classificacao`.
- [ ] Implementar `TabelaVerdadeService` e `TabelaVerdadeResource`.

## Testes Exigidos
- [ ] Testes unitários para fórmulas simples (ex: `P ^ Q`, `~P`).
- [ ] Testes unitários com precedência de operadores e parênteses aninhados.
- [ ] Teste com classificação automática de Tautologia (ex: `P v ~P`), Contradição (ex: `P ^ ~P`) e Contingência.

## Labels / Estimativa
`~backend` `~logica` `~algoritmo` · Estimativa: 8 pts · Prioridade: **P1** · Sprint: **Sprint 1**
