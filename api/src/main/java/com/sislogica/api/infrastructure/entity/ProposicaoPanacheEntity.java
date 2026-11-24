package com.sislogica.api.infrastructure.entity;

import com.sislogica.api.domain.model.Quantificador;
import com.sislogica.api.domain.model.Termo;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "proposicoes")
public class ProposicaoPanacheEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "quantificador", nullable = false, length = 1)
    public Quantificador quantificador;

    @Enumerated(EnumType.STRING)
    @Column(name = "sujeito", nullable = false, length = 1)
    public Termo sujeito;

    @Enumerated(EnumType.STRING)
    @Column(name = "predicado", nullable = false, length = 1)
    public Termo predicado;

    @Column(name = "transliteracao")
    public String transliteracao;
}
