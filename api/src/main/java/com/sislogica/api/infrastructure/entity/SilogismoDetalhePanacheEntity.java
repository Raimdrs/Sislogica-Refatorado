package com.sislogica.api.infrastructure.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity
@Table(name = "silogismo_detalhes")
public class SilogismoDetalhePanacheEntity extends PanacheEntity {

    @Column(name = "modo", length = 40)
    public String modo;

    @Column(name = "nome", length = 60)
    public String nome;

    @Column(name = "valido", nullable = false)
    public boolean valido;

    @Column(name = "reduzivel", nullable = false)
    public boolean reduzivel;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "erros")
    public List<String> erros;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "reducao_id", unique = true)
    public ReducaoPanacheEntity reducao;
}
