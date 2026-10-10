package com.sislogica.api.dto;

import com.sislogica.api.domain.model.Turma;

import java.time.Instant;

public record TurmaResponseDTO(
        Long id,
        String codigo,
        String professor,
        String descricao,
        Instant criadoEm
) {

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
