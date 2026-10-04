package com.sislogica.api.controller;

import com.sislogica.api.infrastructure.entity.SilogismoPanacheEntity;
import com.sislogica.api.service.SilogismoService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SilogismoResourceUnitTest {

    @Test
    @DisplayName("Deve retornar status 200 e lista de silogismos da turma")
    void deveRetornarStatus200EListaDeSilogismos() {
        SilogismoPanacheEntity entity = new SilogismoPanacheEntity();
        entity.id = 1L;
        entity.titulo = "Barbara Teste";
        entity.premissaMaior = "Todo M é P";
        entity.premissaMenor = "Todo S é M";
        entity.conclusao = "Todo S é P";
        entity.modo = "BARBARA";
        entity.turmaId = 1L;
        entity.valido = true;

        SilogismoService stubService = new SilogismoService() {
            @Override
            public List<SilogismoPanacheEntity> buscarPorTurmaPaginado(Long turmaId, int pagina, int tamanho) {
                return List.of(entity);
            }
        };

        SilogismoResource resource = new SilogismoResource();
        resource.silogismoService = stubService;

        Response response = resource.listarSilogismosDaTurma(1L, 0, 10);

        assertEquals(200, response.getStatus());
        assertNotNull(response.getEntity());
        assertEquals(List.of(entity), response.getEntity());
    }
}

