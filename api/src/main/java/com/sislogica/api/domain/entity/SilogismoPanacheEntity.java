package com.sislogica.api.domain; 

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "silogismos")
public class SilogismoPanacheEntity extends PanacheEntity {

    public String premissaMaior;
    public String premissaMenor;
    public String conclusao;
    public boolean valido;

    @Column(name = "turma_id", nullable = false)
    public Long turmaId; 
}
