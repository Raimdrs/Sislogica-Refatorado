package br.com.sislogica.domain.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "silogismos")
public class SilogismoPanacheEntity extends PanacheEntity {

    public String premissaMaior;
    public String premissaMenor;
    public String conclusao;
    public boolean valido;

    @ManyToOne
    @JoinColumn(name = "turma_id", nullable = false)
    public Turma turma;
}
