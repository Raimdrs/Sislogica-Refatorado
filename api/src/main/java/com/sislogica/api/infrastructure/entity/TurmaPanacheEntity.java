package com.sislogica.api.infrastructure.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "turmas")
public class TurmaPanacheEntity extends PanacheEntity {

    @Column(name = "codigo", nullable = false, unique = true, length = 64)
    public String codigo;

    @Column(name = "professor", nullable = false, length = 100)
    public String professor;

    @Column(name = "descricao")
    public String descricao;

    @CreationTimestamp
    @Column(name = "criado_em", updatable = false)
    public Instant criadoEm;
}
