package com.sislogica.api.dto;

import com.sislogica.api.domain.model.Turma;
import com.sislogica.api.infrastructure.entity.TurmaPanacheEntity;

import java.time.Instant;

public class TurmaResponseDTO {

    public Long id;
    public String codigo;
    public String professor;
    public String descricao;
    public Instant criadoEm;

    public TurmaResponseDTO() {
    }

    public TurmaResponseDTO(Long id, String codigo, String professor, String descricao, Instant criadoEm) {
        this.id = id;
        this.codigo = codigo;
        this.professor = professor;
        this.descricao = descricao;
        this.criadoEm = criadoEm;
    }

    public static TurmaResponseDTO fromEntity(TurmaPanacheEntity entity) {
        if (entity == null) {
            return null;
        }
        return new TurmaResponseDTO(
                entity.id,
                entity.codigo,
                entity.professor,
                entity.descricao,
                entity.criadoEm
        );
    }

    public static TurmaResponseDTO fromDomain(Turma domain) {
        if (domain == null) {
            return null;
        }
        return new TurmaResponseDTO(
                domain.getId(),
                domain.getCodigo(),
                domain.getProfessor(),
                domain.getDescricao(),
                domain.getCriadoEm()
        );
    }
}
