package com.sislogica.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SilogismoRequestDTO {

    @NotBlank(message = "O título é obrigatório")
    @Size(max = 150, message = "O título deve ter no máximo 150 caracteres")
    public String titulo;

    @NotNull(message = "A premissa maior é obrigatória")
    @Valid
    public ProposicaoDTO premissaMaior;

    @NotNull(message = "A premissa menor é obrigatória")
    @Valid
    public ProposicaoDTO premissaMenor;

    @NotNull(message = "A conclusão é obrigatória")
    @Valid
    public ProposicaoDTO conclusao;

    @NotBlank(message = "O modo é obrigatório")
    @Size(max = 40, message = "O modo deve ter no máximo 40 caracteres")
    public String modo;

    @NotNull(message = "O ID da turma é obrigatório")
    public Long turmaId;

    public SilogismoRequestDTO() {
    }

    public SilogismoRequestDTO(String titulo, ProposicaoDTO premissaMaior, ProposicaoDTO premissaMenor,
                               ProposicaoDTO conclusao, String modo, Long turmaId) {
        this.titulo = titulo;
        this.premissaMaior = premissaMaior;
        this.premissaMenor = premissaMenor;
        this.conclusao = conclusao;
        this.modo = modo;
        this.turmaId = turmaId;
    }
}
