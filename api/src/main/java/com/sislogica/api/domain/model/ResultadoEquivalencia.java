package com.sislogica.api.domain.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Resultado de domínio da validação de equivalência semântica entre duas fórmulas proposicionais (A ≡ B).
 * Classe 100% pura de domínio, sem dependências de frameworks.
 */
public record ResultadoEquivalencia(
        String formulaA,
        String formulaB,
        boolean equivalentes,
        List<String> variaveis,
        Map<String, Boolean> contraexemplo,
        List<LinhaTabelaVerdade> tabelaVerdade
) {
    public ResultadoEquivalencia {
        if (formulaA == null || formulaA.isBlank()) {
            throw new IllegalArgumentException("A fórmula A é obrigatória.");
        }
        if (formulaB == null || formulaB.isBlank()) {
            throw new IllegalArgumentException("A fórmula B é obrigatória.");
        }
        variaveis = variaveis != null ? List.copyOf(variaveis) : List.of();
        contraexemplo = contraexemplo != null
                ? Collections.unmodifiableMap(new LinkedHashMap<>(contraexemplo))
                : null;
        tabelaVerdade = tabelaVerdade != null ? List.copyOf(tabelaVerdade) : List.of();
    }
}
