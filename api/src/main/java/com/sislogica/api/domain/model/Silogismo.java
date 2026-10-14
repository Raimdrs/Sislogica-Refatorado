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
    private Long turmaId;
    private SilogismoDetalhe detalhe;
    private Instant criadoEm;

    public Silogismo() {
    }

    public Silogismo(Long id, String titulo, Proposicao premissaMaior, Proposicao premissaMenor,
                     Proposicao conclusao, Long turmaId, SilogismoDetalhe detalhe, Instant criadoEm) {
        this.id = id;
        this.titulo = titulo;
        this.premissaMaior = premissaMaior;
        this.premissaMenor = premissaMenor;
        this.conclusao = conclusao;
        this.turmaId = turmaId;
        this.detalhe = detalhe;
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

    public Long getTurmaId() {
        return turmaId;
    }

    public void setTurmaId(Long turmaId) {
        this.turmaId = turmaId;
    }

    public SilogismoDetalhe getDetalhe() {
        return detalhe;
    }

    public void setDetalhe(SilogismoDetalhe detalhe) {
        this.detalhe = detalhe;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Instant criadoEm) {
        this.criadoEm = criadoEm;
    }
}
