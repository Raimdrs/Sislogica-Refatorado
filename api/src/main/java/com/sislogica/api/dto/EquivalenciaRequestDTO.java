package com.sislogica.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EquivalenciaRequestDTO(
        @NotBlank(message = "A fórmula A é obrigatória")
        @Size(max = 500, message = "A fórmula A deve ter no máximo 500 caracteres")
        String formulaA,

        @NotBlank(message = "A fórmula B é obrigatória")
        @Size(max = 500, message = "A fórmula B deve ter no máximo 500 caracteres")
        String formulaB
) {}
