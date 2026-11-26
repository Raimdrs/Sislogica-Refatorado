package com.sislogica.api.domain.logic;

import java.util.Map;
import java.util.Set;

/**
 * Árvore Sintática Abstrata (AST) para fórmulas da lógica proposicional clássica.
 * Pertence exclusivamente à camada de domínio puro.
 */
public sealed interface FormulaProposicional
        permits FormulaProposicional.Variavel,
                FormulaProposicional.Negacao,
                FormulaProposicional.Binaria {

    boolean avaliar(Map<String, Boolean> valoracao);

    void coletarVariaveis(Set<String> destino);

    enum OperadorBinario {
        CONJUNCAO,
        DISJUNCAO,
        IMPLICACAO,
        BICONDICIONAL
    }

    record Variavel(String nome) implements FormulaProposicional {
        @Override
        public boolean avaliar(Map<String, Boolean> valoracao) {
            Boolean valor = valoracao.get(nome);
            if (valor == null) {
                throw new IllegalArgumentException("Variável sem valoração definida: " + nome);
            }
            return valor;
        }

        @Override
        public void coletarVariaveis(Set<String> destino) {
            destino.add(nome);
        }
    }

    record Negacao(FormulaProposicional operando) implements FormulaProposicional {
        @Override
        public boolean avaliar(Map<String, Boolean> valoracao) {
            return !operando.avaliar(valoracao);
        }

        @Override
        public void coletarVariaveis(Set<String> destino) {
            operando.coletarVariaveis(destino);
        }
    }

    record Binaria(
            OperadorBinario operador,
            FormulaProposicional esquerda,
            FormulaProposicional direita
    ) implements FormulaProposicional {
        @Override
        public boolean avaliar(Map<String, Boolean> valoracao) {
            boolean esq = esquerda.avaliar(valoracao);
            boolean dir = direita.avaliar(valoracao);
            return switch (operador) {
                case CONJUNCAO -> esq && dir;
                case DISJUNCAO -> esq || dir;
                case IMPLICACAO -> !esq || dir;
                case BICONDICIONAL -> esq == dir;
            };
        }

        @Override
        public void coletarVariaveis(Set<String> destino) {
            esquerda.coletarVariaveis(destino);
            direita.coletarVariaveis(destino);
        }
    }
}
