package com.sislogica.api.domain.model;

/**
 * Termos integrantes de uma proposição categórica em um silogismo aristotélico:
 * T - Termo Maior (predicado da conclusão)
 * t - Termo Menor (sujeito da conclusão)
 * M - Termo Médio (presente nas premissas e ausente na conclusão)
 */
public enum Termo {
    T("Termo Maior"),
    t("Termo Menor"),
    M("Termo Médio");

    private final String descricao;

    Termo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
