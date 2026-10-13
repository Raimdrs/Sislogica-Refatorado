package com.sislogica.api.domain.model;

/**
 * Quantificadores clássicos das proposições categóricas aristotélicas:
 * A - Universal Afirmativa (Todo S é P)
 * E - Universal Negativa (Nenhum S é P)
 * I - Particular Afirmativa (Algum S é P)
 * O - Particular Negativa (Algum S não é P)
 */
public enum Quantificador {
    A("Universal Afirmativa"),
    E("Universal Negativa"),
    I("Particular Afirmativa"),
    O("Particular Negativa");

    private final String descricao;

    Quantificador(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
