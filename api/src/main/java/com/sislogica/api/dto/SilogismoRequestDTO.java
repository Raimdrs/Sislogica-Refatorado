package com.sislogica.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SilogismoRequestDTO {

    @NotBlank(message = "O título é obrigatório")
    @Size(max = 150, message = "O título deve ter no máximo 150 caracteres")
    public String titulo;

    @NotBlank(message = "A premissa maior é obrigatória")
    public String premissaMaior;

    @NotBlank(message = "A premissa menor é obrigatória")
    public String premissaMenor;

    @NotBlank(message = "A conclusão é obrigatória")
    public String conclusao;

    @NotBlank(message = "O modo é obrigatório")
    @Size(max = 40, message = "O modo deve ter no máximo 40 caracteres")
    public String modo;

    @NotNull(message = "O ID da turma é obrigatório")
    public Long turmaId;

    public SilogismoRequestDTO() {
    }

    public SilogismoRequestDTO(String titulo, String premissaMaior, String premissaMenor,
                               String conclusao, String modo, Long turmaId) {
        this.titulo = titulo;
        this.premissaMaior = premissaMaior;
        this.premissaMenor = premissaMenor;
        this.conclusao = conclusao;
        this.modo = modo;
        this.turmaId = turmaId;
    }
}
