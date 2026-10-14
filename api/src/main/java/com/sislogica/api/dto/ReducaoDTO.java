package com.sislogica.api.dto;

import com.sislogica.api.domain.model.Reducao;
import com.sislogica.api.infrastructure.entity.ReducaoPanacheEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReducaoDTO(
        @NotBlank(message = "O modo da redução é obrigatório")
        @Size(max = 40, message = "O modo da redução deve ter no máximo 40 caracteres")
        String modo,

        @NotBlank(message = "O nome da redução é obrigatório")
        @Size(max = 60, message = "O nome da redução deve ter no máximo 60 caracteres")
        String nome,

        @NotNull(message = "A proposição maior da redução é obrigatória")
        @Valid
        ProposicaoDTO proposicaoMaior,

        @NotNull(message = "A proposição menor da redução é obrigatória")
        @Valid
        ProposicaoDTO proposicaoMenor,

        @NotNull(message = "A conclusão da redução é obrigatória")
        @Valid
        ProposicaoDTO conclusao
) {
    public Reducao toDomain() {
        return new Reducao(
                modo,
                nome,
                proposicaoMaior != null ? proposicaoMaior.toDomain() : null,
                proposicaoMenor != null ? proposicaoMenor.toDomain() : null,
                conclusao != null ? conclusao.toDomain() : null
        );
    }

    public static ReducaoDTO fromDomain(Reducao domain) {
        if (domain == null) {
            return null;
        }
        return new ReducaoDTO(
                domain.getModo(),
                domain.getNome(),
                ProposicaoDTO.fromDomain(domain.getProposicaoMaior()),
                ProposicaoDTO.fromDomain(domain.getProposicaoMenor()),
                ProposicaoDTO.fromDomain(domain.getConclusao())
        );
    }

    public static ReducaoDTO fromEntity(ReducaoPanacheEntity entity) {
        if (entity == null) {
            return null;
        }
        return new ReducaoDTO(
                entity.modo,
                entity.nome,
                ProposicaoDTO.fromEntity(entity.proposicaoMaior),
                ProposicaoDTO.fromEntity(entity.proposicaoMenor),
                ProposicaoDTO.fromEntity(entity.conclusao)
        );
    }
}
