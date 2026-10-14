package com.sislogica.api.infrastructure.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "silogismos")
public class SilogismoPanacheEntity extends PanacheEntity {

    @Column(name = "titulo", nullable = false, length = 150)
    public String titulo;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "premissa_maior_id", nullable = false)
    public ProposicaoPanacheEntity premissaMaior;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "premissa_menor_id", nullable = false)
    public ProposicaoPanacheEntity premissaMenor;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "conclusao_id", nullable = false)
    public ProposicaoPanacheEntity conclusao;

    @Column(name = "turma_id", nullable = false)
    public Long turmaId;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "detalhe_id", unique = true)
    public SilogismoDetalhePanacheEntity detalhe;

    @CreationTimestamp
    @Column(name = "criado_em", updatable = false)
    public Instant criadoEm;
}
