package com.sislogica.api.dto;

import com.sislogica.api.domain.model.LinhaTabelaVerdade;

import java.util.Map;

public record LinhaTabelaVerdadeDTO(
        Map<String, Boolean> valoracao,
        boolean resultadoA,
        boolean resultadoB,
        boolean equivalente
) {
    public static LinhaTabelaVerdadeDTO fromDomain(LinhaTabelaVerdade domain) {
        if (domain == null) {
            return null;
        }
        return new LinhaTabelaVerdadeDTO(
                domain.valoracao(),
                domain.resultadoA(),
                domain.resultadoB(),
                domain.equivalente()
        );
    }
}
