package com.sislogica.api.infrastructure.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "silogismo_reducoes")
public class ReducaoPanacheEntity extends PanacheEntity {

    @Column(name = "modo", nullable = false, length = 40)
    public String modo;

    @Column(name = "nome", nullable = false, length = 60)
    public String nome;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "proposicao_maior_id", nullable = false)
    public ProposicaoPanacheEntity proposicaoMaior;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "proposicao_menor_id", nullable = false)
    public ProposicaoPanacheEntity proposicaoMenor;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "conclusao_id", nullable = false)
    public ProposicaoPanacheEntity conclusao;
}
