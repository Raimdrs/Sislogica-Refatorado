package com.sislogica.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sislogica.api.domain.model.SilogismoDetalhe;
import com.sislogica.api.infrastructure.entity.SilogismoDetalhePanacheEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

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
        ReducaoDTO reducao,

        List<String> erros
) {
    public SilogismoDetalheDTO(String modo, String nome, Boolean valido, Boolean reduzivel, ReducaoDTO reducao) {
        this(modo, nome, valido, reduzivel, reducao, null);
    }

    @JsonIgnore
    @AssertTrue(message = "A redução é obrigatória quando o silogismo é reduzível")
    public boolean isReducaoValida() {
        return !Boolean.TRUE.equals(reduzivel) || reducao != null;
    }

    @JsonIgnore
    @AssertTrue(message = "Os erros são obrigatórios quando o silogismo for inválido")
    public boolean isErrosValidos() {
        if (Boolean.FALSE.equals(valido)) {
            return erros != null && !erros.isEmpty();
        }
        if (Boolean.TRUE.equals(valido)) {
            return erros == null || erros.isEmpty();
        }
        return true;
    }

    public SilogismoDetalhe toDomain() {
        return new SilogismoDetalhe(
                modo,
                nome,
                valido,
                reduzivel,
                reducao != null ? reducao.toDomain() : null,
                erros
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
                ReducaoDTO.fromDomain(domain.getReducao()),
                domain.getErros()
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
                ReducaoDTO.fromEntity(entity.reducao),
                entity.erros
        );
    }
}
