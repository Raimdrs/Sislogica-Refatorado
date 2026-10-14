package com.sislogica.api.dto;

import com.sislogica.api.domain.model.Silogismo;
import com.sislogica.api.infrastructure.entity.SilogismoPanacheEntity;

import java.time.Instant;

public class SilogismoResponseDTO {

    public Long id;
    public String titulo;
    public ProposicaoDTO premissaMaior;
    public ProposicaoDTO premissaMenor;
    public ProposicaoDTO conclusao;
    public Long turmaId;
    public SilogismoDetalheDTO detalhe;
    public Instant criadoEm;

    public SilogismoResponseDTO() {
    }

    public SilogismoResponseDTO(Long id, String titulo, ProposicaoDTO premissaMaior, ProposicaoDTO premissaMenor,
                                ProposicaoDTO conclusao, Long turmaId, SilogismoDetalheDTO detalhe, Instant criadoEm) {
        this.id = id;
        this.titulo = titulo;
        this.premissaMaior = premissaMaior;
        this.premissaMenor = premissaMenor;
        this.conclusao = conclusao;
        this.turmaId = turmaId;
        this.detalhe = detalhe;
        this.criadoEm = criadoEm;
    }

    public static SilogismoResponseDTO fromEntity(SilogismoPanacheEntity entity) {
        if (entity == null) {
            return null;
        }
        return new SilogismoResponseDTO(
                entity.id,
                entity.titulo,
                ProposicaoDTO.fromEntity(entity.premissaMaior),
                ProposicaoDTO.fromEntity(entity.premissaMenor),
                ProposicaoDTO.fromEntity(entity.conclusao),
                entity.turmaId,
                SilogismoDetalheDTO.fromEntity(entity.detalhe),
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
                ProposicaoDTO.fromDomain(domain.getPremissaMaior()),
                ProposicaoDTO.fromDomain(domain.getPremissaMenor()),
                ProposicaoDTO.fromDomain(domain.getConclusao()),
                domain.getTurmaId(),
                SilogismoDetalheDTO.fromDomain(domain.getDetalhe()),
                domain.getCriadoEm()
        );
    }
}
