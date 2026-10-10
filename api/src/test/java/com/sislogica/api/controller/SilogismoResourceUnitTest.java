package com.sislogica.api.controller;

import com.sislogica.api.dto.SilogismoRequestDTO;
import com.sislogica.api.dto.SilogismoResponseDTO;
import com.sislogica.api.infrastructure.entity.SilogismoPanacheEntity;
import com.sislogica.api.service.SilogismoService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SilogismoResourceUnitTest {

    private SilogismoResource resource;
    private SilogismoResponseDTO mockResponseDTO;

    @BeforeEach
    void setUp() {
        resource = new SilogismoResource();
        mockResponseDTO = new SilogismoResponseDTO(
                1L,
                "Barbara Teste",
                "Todo M é P",
                "Todo S é M",
                "Todo S é P",
                "BARBARA",
                1L,
                true,
                Instant.now()
        );
    }

    @Test
    @DisplayName("POST /api/v1/silogismos: Deve criar silogismo e retornar status 201 com Location")
    void deveCriarSilogismoERetornar201ComLocation() {
        SilogismoRequestDTO requestDTO = new SilogismoRequestDTO(
                "Barbara Teste",
                "Todo M é P",
                "Todo S é M",
                "Todo S é P",
                "BARBARA",
                1L
        );

        resource.silogismoService = new SilogismoService() {
            @Override
            public SilogismoResponseDTO criar(SilogismoRequestDTO dto) {
                return mockResponseDTO;
            }
        };

        Response response = resource.criarSilogismo(requestDTO);

        assertEquals(201, response.getStatus());
        assertEquals("/api/v1/silogismos/1", response.getLocation().toString());
        assertEquals(mockResponseDTO, response.getEntity());
    }

    @Test
    @DisplayName("GET /api/v1/silogismos: Deve listar silogismos paginados e retornar status 200")
    void deveListarSilogismosPaginadosERetornar200() {
        resource.silogismoService = new SilogismoService() {
            @Override
            public List<SilogismoResponseDTO> listarPaginado(int pagina, int tamanho, String modo) {
                return List.of(mockResponseDTO);
            }
        };

        Response response = resource.listarSilogismos(0, 10, "BARBARA");

        assertEquals(200, response.getStatus());
        assertEquals(List.of(mockResponseDTO), response.getEntity());
    }

    @Test
    @DisplayName("GET /api/v1/silogismos/{id}: Deve retornar status 200 quando silogismo existir")
    void deveRetornar200QuandoSilogismoExistir() {
        resource.silogismoService = new SilogismoService() {
            @Override
            public Optional<SilogismoResponseDTO> buscarPorId(Long id) {
                return Optional.of(mockResponseDTO);
            }
        };

        Response response = resource.buscarSilogismoPorId(1L);

        assertEquals(200, response.getStatus());
        assertEquals(mockResponseDTO, response.getEntity());
    }

    @Test
    @DisplayName("GET /api/v1/silogismos/{id}: Deve retornar status 404 quando silogismo não existir")
    void deveRetornar404QuandoSilogismoNaoExistir() {
        resource.silogismoService = new SilogismoService() {
            @Override
            public Optional<SilogismoResponseDTO> buscarPorId(Long id) {
                return Optional.empty();
            }
        };

        Response response = resource.buscarSilogismoPorId(999L);

        assertEquals(404, response.getStatus());
    }

    @Test
    @DisplayName("PUT /api/v1/silogismos/{id}: Deve retornar status 200 ao atualizar silogismo existente")
    void deveRetornar200AoAtualizarSilogismo() {
        SilogismoRequestDTO requestDTO = new SilogismoRequestDTO(
                "Barbara Atualizado",
                "Todo M é P",
                "Todo S é M",
                "Todo S é P",
                "BARBARA",
                1L
        );

        resource.silogismoService = new SilogismoService() {
            @Override
            public Optional<SilogismoResponseDTO> atualizar(Long id, SilogismoRequestDTO dto) {
                return Optional.of(mockResponseDTO);
            }
        };

        Response response = resource.atualizarSilogismo(1L, requestDTO);

        assertEquals(200, response.getStatus());
        assertEquals(mockResponseDTO, response.getEntity());
    }

    @Test
    @DisplayName("PUT /api/v1/silogismos/{id}: Deve retornar status 404 ao tentar atualizar silogismo inexistente")
    void deveRetornar404AoAtualizarSilogismoInexistente() {
        SilogismoRequestDTO requestDTO = new SilogismoRequestDTO(
                "Barbara Atualizado",
                "Todo M é P",
                "Todo S é M",
                "Todo S é P",
                "BARBARA",
                1L
        );

        resource.silogismoService = new SilogismoService() {
            @Override
            public Optional<SilogismoResponseDTO> atualizar(Long id, SilogismoRequestDTO dto) {
                return Optional.empty();
            }
        };

        Response response = resource.atualizarSilogismo(999L, requestDTO);

        assertEquals(404, response.getStatus());
    }

    @Test
    @DisplayName("DELETE /api/v1/silogismos/{id}: Deve retornar status 204 ao remover silogismo")
    void deveRetornar204AoRemoverSilogismo() {
        resource.silogismoService = new SilogismoService() {
            @Override
            public boolean deletar(Long id) {
                return true;
            }
        };

        Response response = resource.deletarSilogismo(1L);

        assertEquals(204, response.getStatus());
    }

    @Test
    @DisplayName("DELETE /api/v1/silogismos/{id}: Deve retornar status 404 ao tentar remover silogismo inexistente")
    void deveRetornar404AoRemoverSilogismoInexistente() {
        resource.silogismoService = new SilogismoService() {
            @Override
            public boolean deletar(Long id) {
                return false;
            }
        };

        Response response = resource.deletarSilogismo(999L);

        assertEquals(404, response.getStatus());
    }

    @Test
    @DisplayName("GET /api/v1/turmas/{turmaId}/silogismos: Deve retornar status 200 e lista de silogismos da turma")
    void deveRetornarStatus200EListaDeSilogismosDaTurma() {
        resource.silogismoService = new SilogismoService() {
            @Override
            public List<SilogismoResponseDTO> buscarPorTurmaPaginado(Long turmaId, int pagina, int tamanho) {
                return List.of(mockResponseDTO);
            }
        };

        Response response = resource.listarSilogismosDaTurma(1L, 0, 10);

        assertEquals(200, response.getStatus());
        assertNotNull(response.getEntity());
        assertEquals(List.of(mockResponseDTO), response.getEntity());
    }
}
