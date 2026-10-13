package com.sislogica.api.domain.model;

import java.util.Objects;

/**
 * Entidade pura de domínio representando uma Proposição Categórica utilizada na construção de um Silogismo.
 * Possui quantificador obrigatório (A, E, I ou O), termos em ordem (sujeito e predicado: T, t ou M)
 * e transliteração opcional em texto plano.
 */
public class Proposicao {

    private Long id;
    private Quantificador quantificador;
    private Termo sujeito;
    private Termo predicado;
    private String transliteracao;

    public Proposicao() {
    }

    public Proposicao(Quantificador quantificador, Termo sujeito, Termo predicado) {
        this(null, quantificador, sujeito, predicado, null);
    }

    public Proposicao(Quantificador quantificador, Termo sujeito, Termo predicado, String transliteracao) {
        this(null, quantificador, sujeito, predicado, transliteracao);
    }

    public Proposicao(Long id, Quantificador quantificador, Termo sujeito, Termo predicado, String transliteracao) {
        validarCamposObrigatorios(quantificador, sujeito, predicado);
        this.id = id;
        this.quantificador = quantificador;
        this.sujeito = sujeito;
        this.predicado = predicado;
        this.transliteracao = transliteracao;
    }

    private static void validarCamposObrigatorios(Quantificador quantificador, Termo sujeito, Termo predicado) {
        if (quantificador == null) {
            throw new IllegalArgumentException("O quantificador da proposição é obrigatório (A, E, I ou O)");
        }
        if (sujeito == null) {
            throw new IllegalArgumentException("O primeiro termo (sujeito) da proposição é obrigatório (T, t ou M)");
        }
        if (predicado == null) {
            throw new IllegalArgumentException("O segundo termo (predicado) da proposição é obrigatório (T, t ou M)");
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Quantificador getQuantificador() {
        return quantificador;
    }

    public void setQuantificador(Quantificador quantificador) {
        if (quantificador == null) {
            throw new IllegalArgumentException("O quantificador da proposição é obrigatório (A, E, I ou O)");
        }
        this.quantificador = quantificador;
    }

    public Termo getSujeito() {
        return sujeito;
    }

    public void setSujeito(Termo sujeito) {
        if (sujeito == null) {
            throw new IllegalArgumentException("O primeiro termo (sujeito) da proposição é obrigatório (T, t ou M)");
        }
        this.sujeito = sujeito;
    }

    public Termo getPredicado() {
        return predicado;
    }

    public void setPredicado(Termo predicado) {
        if (predicado == null) {
            throw new IllegalArgumentException("O segundo termo (predicado) da proposição é obrigatório (T, t ou M)");
        }
        this.predicado = predicado;
    }

    public String getTransliteracao() {
        return transliteracao;
    }

    public void setTransliteracao(String transliteracao) {
        this.transliteracao = transliteracao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Proposicao that = (Proposicao) o;
        return Objects.equals(id, that.id)
                && quantificador == that.quantificador
                && sujeito == that.sujeito
                && predicado == that.predicado
                && Objects.equals(transliteracao, that.transliteracao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, quantificador, sujeito, predicado, transliteracao);
    }
}
