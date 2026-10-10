package com.sislogica.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TurmaRequestDTO {

    @NotBlank(message = "O código da turma é obrigatório")
    @Size(max = 64, message = "O código deve ter no máximo 64 caracteres")
    public String codigo;

    @NotBlank(message = "O nome do professor é obrigatório")
    @Size(max = 100, message = "O nome do professor deve ter no máximo 100 caracteres")
    public String professor;

    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
    public String descricao;

    public TurmaRequestDTO() {
    }

    public TurmaRequestDTO(String codigo, String professor, String descricao) {
        this.codigo = codigo;
        this.professor = professor;
        this.descricao = descricao;
    }
}
