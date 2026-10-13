package com.sislogica.api.dto;

import com.sislogica.api.domain.model.Proposicao;
import com.sislogica.api.domain.model.Quantificador;
import com.sislogica.api.domain.model.Termo;
import com.sislogica.api.infrastructure.entity.ProposicaoPanacheEntity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProposicaoDTO(
        @NotNull(message = "O quantificador é obrigatório (A, E, I ou O)")
        Quantificador quantificador,

        @NotNull(message = "O primeiro termo (sujeito) é obrigatório (T, t ou M)")
        Termo sujeito,

        @NotNull(message = "O segundo termo (predicado) é obrigatório (T, t ou M)")
        Termo predicado,

        @Size(max = 500, message = "A transliteração deve ter no máximo 500 caracteres")
        String transliteracao
) {
    public ProposicaoDTO(Quantificador quantificador, Termo sujeito, Termo predicado) {
        this(quantificador, sujeito, predicado, null);
    }

    public Proposicao toDomain() {
        return new Proposicao(quantificador, sujeito, predicado, transliteracao);
    }

    public static ProposicaoDTO fromDomain(Proposicao domain) {
        if (domain == null) {
            return null;
        }
        return new ProposicaoDTO(
                domain.getQuantificador(),
                domain.getSujeito(),
                domain.getPredicado(),
                domain.getTransliteracao()
        );
    }

    public static ProposicaoDTO fromEntity(ProposicaoPanacheEntity entity) {
        if (entity == null) {
            return null;
        }
        return new ProposicaoDTO(
                entity.quantificador,
                entity.sujeito,
                entity.predicado,
                entity.transliteracao
        );
    }
}
