package com.sislogica.api.infrastructure.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "turmas")
public class TurmaPanacheEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

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
