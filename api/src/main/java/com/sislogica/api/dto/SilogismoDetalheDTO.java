package com.sislogica.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sislogica.api.domain.model.SilogismoDetalhe;
import com.sislogica.api.infrastructure.entity.SilogismoDetalhePanacheEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SilogismoDetalheDTO(
        @Size(max = 40, message = "O modo deve ter no máximo 40 caracteres")
        String modo,

        @Size(max = 60, message = "O nome deve ter no máximo 60 caracteres")
        String nome,

        @NotNull(message = "O campo 'valido' é obrigatório")
        Boolean valido,

        @NotNull(message = "O campo 'reduzivel' é obrigatório")
        Boolean reduzivel,

        @Valid
        ReducaoDTO reducao
) {
    @JsonIgnore
    @AssertTrue(message = "A redução é obrigatória quando o silogismo é reduzível")
    public boolean isReducaoValida() {
        return !Boolean.TRUE.equals(reduzivel) || reducao != null;
    }

    public SilogismoDetalhe toDomain() {
        return new SilogismoDetalhe(
                modo,
                nome,
                valido,
                reduzivel,
                reducao != null ? reducao.toDomain() : null
        );
    }

    public static SilogismoDetalheDTO fromDomain(SilogismoDetalhe domain) {
        if (domain == null) {
            return null;
        }
        return new SilogismoDetalheDTO(
                domain.getModo(),
                domain.getNome(),
                domain.getValido(),
                domain.getReduzivel(),
                ReducaoDTO.fromDomain(domain.getReducao())
        );
    }

    public static SilogismoDetalheDTO fromEntity(SilogismoDetalhePanacheEntity entity) {
        if (entity == null) {
            return null;
        }
        return new SilogismoDetalheDTO(
                entity.modo,
                entity.nome,
                entity.valido,
                entity.reduzivel,
                ReducaoDTO.fromEntity(entity.reducao)
        );
    }
}
