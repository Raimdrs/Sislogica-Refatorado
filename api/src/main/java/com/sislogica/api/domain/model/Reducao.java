package com.sislogica.api.domain.model;

import java.util.Objects;

/**
 * Entidade pura de domínio representando a Redução de um Silogismo (ex.: redução para a 1ª figura).
 * Possui modo obrigatório, nome obrigatório e as três proposições (maior, menor e conclusão) obrigatórias.
 */
public class Reducao {

    private Long id;
    private String modo;
    private String nome;
    private Proposicao proposicaoMaior;
    private Proposicao proposicaoMenor;
    private Proposicao conclusao;

    public Reducao() {
    }

    public Reducao(String modo, String nome, Proposicao proposicaoMaior,
                   Proposicao proposicaoMenor, Proposicao conclusao) {
        this(null, modo, nome, proposicaoMaior, proposicaoMenor, conclusao);
    }

    public Reducao(Long id, String modo, String nome, Proposicao proposicaoMaior,
                   Proposicao proposicaoMenor, Proposicao conclusao) {
        validarCamposObrigatorios(modo, nome, proposicaoMaior, proposicaoMenor, conclusao);
        this.id = id;
        this.modo = modo;
        this.nome = nome;
        this.proposicaoMaior = proposicaoMaior;
        this.proposicaoMenor = proposicaoMenor;
        this.conclusao = conclusao;
    }

    private static void validarCamposObrigatorios(String modo, String nome, Proposicao proposicaoMaior,
                                                  Proposicao proposicaoMenor, Proposicao conclusao) {
        if (modo == null || modo.isBlank()) {
            throw new IllegalArgumentException("O modo da redução é obrigatório");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome da redução é obrigatório");
        }
        if (proposicaoMaior == null) {
            throw new IllegalArgumentException("A proposição maior da redução é obrigatória");
        }
        if (proposicaoMenor == null) {
            throw new IllegalArgumentException("A proposição menor da redução é obrigatória");
        }
        if (conclusao == null) {
            throw new IllegalArgumentException("A conclusão da redução é obrigatória");
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
        if (modo == null || modo.isBlank()) {
            throw new IllegalArgumentException("O modo da redução é obrigatório");
        }
        this.modo = modo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome da redução é obrigatório");
        }
        this.nome = nome;
    }

    public Proposicao getProposicaoMaior() {
        return proposicaoMaior;
    }

    public void setProposicaoMaior(Proposicao proposicaoMaior) {
        if (proposicaoMaior == null) {
            throw new IllegalArgumentException("A proposição maior da redução é obrigatória");
        }
        this.proposicaoMaior = proposicaoMaior;
    }

    public Proposicao getProposicaoMenor() {
        return proposicaoMenor;
    }

    public void setProposicaoMenor(Proposicao proposicaoMenor) {
        if (proposicaoMenor == null) {
            throw new IllegalArgumentException("A proposição menor da redução é obrigatória");
        }
        this.proposicaoMenor = proposicaoMenor;
    }

    public Proposicao getConclusao() {
        return conclusao;
    }

    public void setConclusao(Proposicao conclusao) {
        if (conclusao == null) {
            throw new IllegalArgumentException("A conclusão da redução é obrigatória");
        }
        this.conclusao = conclusao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reducao reducao = (Reducao) o;
        return Objects.equals(id, reducao.id)
                && Objects.equals(modo, reducao.modo)
                && Objects.equals(nome, reducao.nome)
                && Objects.equals(proposicaoMaior, reducao.proposicaoMaior)
                && Objects.equals(proposicaoMenor, reducao.proposicaoMenor)
                && Objects.equals(conclusao, reducao.conclusao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, modo, nome, proposicaoMaior, proposicaoMenor, conclusao);
    }
}
