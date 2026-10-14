package com.sislogica.api.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    private List<String> erros = new ArrayList<>();

    public SilogismoDetalhe() {
    }

    public SilogismoDetalhe(String modo, String nome, Boolean valido, Boolean reduzivel, Reducao reducao) {
        this(null, modo, nome, valido, reduzivel, reducao, null);
    }

    public SilogismoDetalhe(String modo, String nome, Boolean valido, Boolean reduzivel, Reducao reducao, List<String> erros) {
        this(null, modo, nome, valido, reduzivel, reducao, erros);
    }

    public SilogismoDetalhe(Long id, String modo, String nome, Boolean valido, Boolean reduzivel, Reducao reducao) {
        this(id, modo, nome, valido, reduzivel, reducao, null);
    }

    public SilogismoDetalhe(Long id, String modo, String nome, Boolean valido, Boolean reduzivel, Reducao reducao, List<String> erros) {
        validarCampos(valido, reduzivel, reducao, erros);
        this.id = id;
        this.modo = modo;
        this.nome = nome;
        this.valido = valido;
        this.reduzivel = reduzivel;
        this.reducao = reducao;
        this.erros = erros != null ? new ArrayList<>(erros) : new ArrayList<>();
    }

    private static void validarCampos(Boolean valido, Boolean reduzivel, Reducao reducao, List<String> erros) {
        if (valido == null) {
            throw new IllegalArgumentException("O campo 'valido' do detalhe do silogismo é obrigatório");
        }
        if (reduzivel == null) {
            throw new IllegalArgumentException("O campo 'reduzivel' do detalhe do silogismo é obrigatório");
        }
        if (reduzivel && reducao == null) {
            throw new IllegalArgumentException("A redução é obrigatória quando o silogismo é reduzível");
        }
        if (!valido && (erros == null || erros.isEmpty())) {
            throw new IllegalArgumentException("Os erros são obrigatórios quando o silogismo for inválido");
        }
        if (valido && erros != null && !erros.isEmpty()) {
            throw new IllegalArgumentException("Um silogismo válido não deve conter erros");
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
        if (!valido && (this.erros == null || this.erros.isEmpty())) {
            throw new IllegalArgumentException("Os erros são obrigatórios quando o silogismo for inválido");
        }
        if (valido && this.erros != null && !this.erros.isEmpty()) {
            throw new IllegalArgumentException("Um silogismo válido não deve conter erros");
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

    public List<String> getErros() {
        return erros != null ? Collections.unmodifiableList(erros) : Collections.emptyList();
    }

    public void setErros(List<String> erros) {
        if (Boolean.FALSE.equals(this.valido) && (erros == null || erros.isEmpty())) {
            throw new IllegalArgumentException("Os erros são obrigatórios quando o silogismo for inválido");
        }
        if (Boolean.TRUE.equals(this.valido) && erros != null && !erros.isEmpty()) {
            throw new IllegalArgumentException("Um silogismo válido não deve conter erros");
        }
        this.erros = erros != null ? new ArrayList<>(erros) : new ArrayList<>();
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
                && Objects.equals(reducao, that.reducao)
                && Objects.equals(erros, that.erros);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, modo, nome, valido, reduzivel, reducao, erros);
    }
}
