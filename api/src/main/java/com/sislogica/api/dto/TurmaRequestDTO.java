package com.sislogica.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TurmaRequestDTO(
        @NotBlank(message = "O código da turma é obrigatório")
        @Size(max = 64, message = "O código deve ter no máximo 64 caracteres")
        String codigo,

        @NotBlank(message = "O nome do professor é obrigatório")
        @Size(max = 100, message = "O nome do professor deve ter no máximo 100 caracteres")
        String professor,

        @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
        String descricao
) {}
