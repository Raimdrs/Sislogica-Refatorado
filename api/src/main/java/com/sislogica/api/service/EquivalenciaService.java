package com.sislogica.api.service;

import com.sislogica.api.domain.logic.ValidadorEquivalenciaDomainService;
import com.sislogica.api.domain.model.ResultadoEquivalencia;
import com.sislogica.api.dto.EquivalenciaRequestDTO;
import com.sislogica.api.dto.EquivalenciaResponseDTO;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EquivalenciaService {

    private final ValidadorEquivalenciaDomainService validador;

    public EquivalenciaService() {
        this(new ValidadorEquivalenciaDomainService());
    }

    public EquivalenciaService(ValidadorEquivalenciaDomainService validador) {
        this.validador = validador;
    }

    public EquivalenciaResponseDTO validar(EquivalenciaRequestDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Os dados da requisição de equivalência são obrigatórios.");
        }
        ResultadoEquivalencia resultado = validador.validar(dto.formulaA(), dto.formulaB());
        return EquivalenciaResponseDTO.fromDomain(resultado);
    }
}
