package com.sislogica.api.domain.model;

import java.time.Instant;

/**
 * Entidade pura de domínio representando um Silogismo Lógico Aristotélico.
 * Conforme princípios da Clean Architecture, esta classe é isolada de frameworks.
 */
public class Silogismo {

    private Long id;
    private String titulo;
    private Proposicao premissaMaior;
    private Proposicao premissaMenor;
    private Proposicao conclusao;
    private String modo;
    private Long turmaId;
    private boolean valido;
    private Instant criadoEm;

    public Silogismo() {
    }

    public Silogismo(Long id, String titulo, Proposicao premissaMaior, Proposicao premissaMenor,
                     Proposicao conclusao, String modo, Long turmaId, boolean valido, Instant criadoEm) {
        this.id = id;
        this.titulo = titulo;
        this.premissaMaior = premissaMaior;
        this.premissaMenor = premissaMenor;
        this.conclusao = conclusao;
        this.modo = modo;
        this.turmaId = turmaId;
        this.valido = valido;
        this.criadoEm = criadoEm != null ? criadoEm : Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Proposicao getPremissaMaior() {
        return premissaMaior;
    }

    public void setPremissaMaior(Proposicao premissaMaior) {
        this.premissaMaior = premissaMaior;
    }

    public Proposicao getPremissaMenor() {
        return premissaMenor;
    }

    public void setPremissaMenor(Proposicao premissaMenor) {
        this.premissaMenor = premissaMenor;
    }

    public Proposicao getConclusao() {
        return conclusao;
    }

    public void setConclusao(Proposicao conclusao) {
        this.conclusao = conclusao;
    }

    public String getModo() {
        return modo;
    }

    public void setModo(String modo) {
        this.modo = modo;
    }

    public Long getTurmaId() {
        return turmaId;
    }

    public void setTurmaId(Long turmaId) {
        this.turmaId = turmaId;
    }

    public boolean isValido() {
        return valido;
    }

    public void setValido(boolean valido) {
        this.valido = valido;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Instant criadoEm) {
        this.criadoEm = criadoEm;
    }
}

