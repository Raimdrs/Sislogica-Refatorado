package com.sislogica.api.dto;

import com.sislogica.api.domain.model.ResultadoEquivalencia;

import java.util.List;
import java.util.Map;

public record EquivalenciaResponseDTO(
        String formulaA,
        String formulaB,
        boolean equivalentes,
        List<String> variaveis,
        Map<String, Boolean> contraexemplo,
        List<LinhaTabelaVerdadeDTO> tabelaVerdade
) {
    public static EquivalenciaResponseDTO fromDomain(ResultadoEquivalencia domain) {
        if (domain == null) {
            return null;
        }
        return new EquivalenciaResponseDTO(
                domain.formulaA(),
                domain.formulaB(),
                domain.equivalentes(),
                domain.variaveis(),
                domain.contraexemplo(),
                domain.tabelaVerdade().stream()
                        .map(LinhaTabelaVerdadeDTO::fromDomain)
                        .toList()
        );
    }
}
