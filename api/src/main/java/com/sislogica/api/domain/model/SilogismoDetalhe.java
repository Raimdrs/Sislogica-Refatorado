package com.sislogica.api.domain.model;

import java.util.Objects;

/**
 * Entidade pura de domínio representando os detalhes de análise e classificação de um Silogismo (1:1).
 * Possui modo opcional (ex.: AAA-1, EAE-1, EAE-2), nome opcional (ex.: Barbara, Celarent, Cesare),
 * valido obrigatório, reduzivel obrigatório e reducao obrigatória caso reduzivel seja verdadeiro.
 */
public class SilogismoDetalhe {

    private Long id;
    private String modo;
    private String nome;
    private Boolean valido;
    private Boolean reduzivel;
    private Reducao reducao;

    public SilogismoDetalhe() {
    }

    public SilogismoDetalhe(String modo, String nome, Boolean valido, Boolean reduzivel, Reducao reducao) {
        this(null, modo, nome, valido, reduzivel, reducao);
    }

    public SilogismoDetalhe(Long id, String modo, String nome, Boolean valido, Boolean reduzivel, Reducao reducao) {
        validarCampos(valido, reduzivel, reducao);
        this.id = id;
        this.modo = modo;
        this.nome = nome;
        this.valido = valido;
        this.reduzivel = reduzivel;
        this.reducao = reducao;
    }

    private static void validarCampos(Boolean valido, Boolean reduzivel, Reducao reducao) {
        if (valido == null) {
            throw new IllegalArgumentException("O campo 'valido' do detalhe do silogismo é obrigatório");
        }
        if (reduzivel == null) {
            throw new IllegalArgumentException("O campo 'reduzivel' do detalhe do silogismo é obrigatório");
        }
        if (reduzivel && reducao == null) {
            throw new IllegalArgumentException("A redução é obrigatória quando o silogismo é reduzível");
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModo() {
        return modo;
    }

    public void setModo(String modo) {
        this.modo = modo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Boolean getValido() {
        return valido;
    }

    public boolean isValido() {
        return Boolean.TRUE.equals(valido);
    }

    public void setValido(Boolean valido) {
        if (valido == null) {
            throw new IllegalArgumentException("O campo 'valido' do detalhe do silogismo é obrigatório");
        }
        this.valido = valido;
    }

    public Boolean getReduzivel() {
        return reduzivel;
    }

    public boolean isReduzivel() {
        return Boolean.TRUE.equals(reduzivel);
    }

    public void setReduzivel(Boolean reduzivel) {
        if (reduzivel == null) {
            throw new IllegalArgumentException("O campo 'reduzivel' do detalhe do silogismo é obrigatório");
        }
        this.reduzivel = reduzivel;
    }

    public Reducao getReducao() {
        return reducao;
    }

    public void setReducao(Reducao reducao) {
        if (Boolean.TRUE.equals(this.reduzivel) && reducao == null) {
            throw new IllegalArgumentException("A redução é obrigatória quando o silogismo é reduzível");
        }
        this.reducao = reducao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SilogismoDetalhe that = (SilogismoDetalhe) o;
        return Objects.equals(id, that.id)
                && Objects.equals(modo, that.modo)
                && Objects.equals(nome, that.nome)
                && Objects.equals(valido, that.valido)
                && Objects.equals(reduzivel, that.reduzivel)
                && Objects.equals(reducao, that.reducao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, modo, nome, valido, reduzivel, reducao);
    }
}
