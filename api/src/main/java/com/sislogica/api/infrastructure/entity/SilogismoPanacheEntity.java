package com.sislogica.api.infrastructure.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "silogismos")
public class SilogismoPanacheEntity extends PanacheEntity {

    @Column(name = "titulo", nullable = false, length = 150)
    public String titulo;

    @Column(name = "premissa_maior", nullable = false)
    public String premissaMaior;

    @Column(name = "premissa_menor", nullable = false)
    public String premissaMenor;

    @Column(name = "conclusao", nullable = false)
    public String conclusao;

    @Column(name = "modo", nullable = false, length = 40)
    public String modo;

    @Column(name = "turma_id", nullable = false)
    public Long turmaId;

    @Column(name = "valido", nullable = false)
    public boolean valido = true;

    @Column(name = "criado_em")
    public Instant criadoEm = Instant.now();
}

