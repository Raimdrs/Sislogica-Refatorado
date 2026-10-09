package com.sislogica.api.dto;

import com.sislogica.api.domain.model.Silogismo;
import com.sislogica.api.infrastructure.entity.SilogismoPanacheEntity;

import java.time.Instant;

public class SilogismoResponseDTO {

    public Long id;
    public String titulo;
    public String premissaMaior;
    public String premissaMenor;
    public String conclusao;
    public String modo;
    public Long turmaId;
    public boolean valido;
    public Instant criadoEm;

    public SilogismoResponseDTO() {
    }

    public SilogismoResponseDTO(Long id, String titulo, String premissaMaior, String premissaMenor,
                                String conclusao, String modo, Long turmaId, boolean valido, Instant criadoEm) {
        this.id = id;
        this.titulo = titulo;
        this.premissaMaior = premissaMaior;
        this.premissaMenor = premissaMenor;
        this.conclusao = conclusao;
        this.modo = modo;
        this.turmaId = turmaId;
        this.valido = valido;
        this.criadoEm = criadoEm;
    }

    public static SilogismoResponseDTO fromEntity(SilogismoPanacheEntity entity) {
        if (entity == null) {
            return null;
        }
        return new SilogismoResponseDTO(
                entity.id,
                entity.titulo,
                entity.premissaMaior,
                entity.premissaMenor,
                entity.conclusao,
                entity.modo,
                entity.turmaId,
                entity.valido,
                entity.criadoEm
        );
    }

    public static SilogismoResponseDTO fromDomain(Silogismo domain) {
        if (domain == null) {
            return null;
        }
        return new SilogismoResponseDTO(
                domain.getId(),
                domain.getTitulo(),
                domain.getPremissaMaior(),
                domain.getPremissaMenor(),
                domain.getConclusao(),
                domain.getModo(),
                domain.getTurmaId(),
                domain.isValido(),
                domain.getCriadoEm()
        );
    }
}
