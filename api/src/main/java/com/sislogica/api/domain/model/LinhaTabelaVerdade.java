package com.sislogica.api.domain.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Representa uma linha da tabela-verdade comparando os resultados de duas fórmulas proposicionais
 * sob uma mesma atribuição de valores-verdade (valoração).
 */
public record LinhaTabelaVerdade(
        Map<String, Boolean> valoracao,
        boolean resultadoA,
        boolean resultadoB,
        boolean equivalente
) {
    public LinhaTabelaVerdade {
        if (valoracao == null) {
            throw new IllegalArgumentException("A valoração da linha da tabela-verdade é obrigatória.");
        }
        valoracao = Collections.unmodifiableMap(new LinkedHashMap<>(valoracao));
    }
}
