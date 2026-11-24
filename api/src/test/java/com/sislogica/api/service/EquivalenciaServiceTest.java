package com.sislogica.api.service;

import com.sislogica.api.dto.EquivalenciaRequestDTO;
import com.sislogica.api.dto.EquivalenciaResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EquivalenciaServiceTest {

    private EquivalenciaService service;

    @BeforeEach
    void setUp() {
        service = new EquivalenciaService();
    }

    @Test
    @DisplayName("Deve retornar DTO com equivalentes=true para De Morgan")
    void deveValidarEquivalenciaVerdadeira() {
        EquivalenciaResponseDTO response = service.validar(new EquivalenciaRequestDTO("~(P ^ Q)", "~P v ~Q"));

        assertTrue(response.equivalentes());
        assertNull(response.contraexemplo());
        assertEquals(List.of("P", "Q"), response.variaveis());
        assertEquals(4, response.tabelaVerdade().size());
    }

    @Test
    @DisplayName("Deve retornar DTO com equivalentes=false e contraexemplo para fórmulas não equivalentes")
    void deveValidarEquivalenciaFalsaComContraexemplo() {
        EquivalenciaResponseDTO response = service.validar(new EquivalenciaRequestDTO("P -> Q", "Q -> P"));

        assertFalse(response.equivalentes());
        assertEquals(Map.of("P", true, "Q", false), response.contraexemplo());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando DTO for nulo")
    void deveRejeitarDtoNulo() {
        assertThrows(IllegalArgumentException.class, () -> service.validar(null));
    }
}
