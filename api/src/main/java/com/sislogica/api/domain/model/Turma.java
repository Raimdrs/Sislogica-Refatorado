package com.sislogica.api.domain.model;

import java.time.Instant;

/**
 * Entidade pura de domínio representando uma Turma/Classe.
 * Conforme princípios da Clean Architecture, esta classe não contém anotações de framework (JPA/Quarkus).
 */
public class Turma {

    private Long id;
    private String codigo;
    private String professor;
    private String descricao;
    private Instant criadoEm;

    public Turma() {
    }

    public Turma(Long id, String codigo, String professor, String descricao, Instant criadoEm) {
        this.id = id;
        this.codigo = codigo;
        this.professor = professor;
        this.descricao = descricao;
        this.criadoEm = criadoEm != null ? criadoEm : Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Instant criadoEm) {
        this.criadoEm = criadoEm;
    }
}

