package com.sislogica.api.controller;

import com.sislogica.api.dto.TurmaRequestDTO;
import com.sislogica.api.dto.TurmaResponseDTO;
import com.sislogica.api.service.TurmaService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TurmaResourceUnitTest {

    private TurmaResource resource;
    private TurmaResponseDTO mockDTO;

    @BeforeEach
    void setUp() {
        resource = new TurmaResource();
        mockDTO = new TurmaResponseDTO(1L, "LOG-01", "Prof. Aristóteles", "Descrição", Instant.now());
    }

    @Test
    @DisplayName("POST /api/v1/turmas: Deve retornar 201 com cabeçalho Location")
    void deveCriarTurmaComSucesso() {
        resource.turmaService = new TurmaService() {
            @Override
            public TurmaResponseDTO criar(TurmaRequestDTO dto) {
                return mockDTO;
            }
        };

        TurmaRequestDTO request = new TurmaRequestDTO("LOG-01", "Prof. Aristóteles", "Descrição");
        Response response = resource.criarTurma(request);

        assertEquals(201, response.getStatus());
        assertEquals("/api/v1/turmas/1", response.getLocation().toString());
        assertEquals(mockDTO, response.getEntity());
    }

    @Test
    @DisplayName("POST /api/v1/turmas: Deve retornar 400 em caso de código duplicado")
    void deveRetornar400AoCriarComCodigoDuplicado() {
        resource.turmaService = new TurmaService() {
            @Override
            public TurmaResponseDTO criar(TurmaRequestDTO dto) {
                throw new IllegalArgumentException("Código duplicado");
            }
        };

        TurmaRequestDTO request = new TurmaRequestDTO("LOG-01", "Prof. Aristóteles", "Descrição");
        Response response = resource.criarTurma(request);

        assertEquals(400, response.getStatus());
    }

    @Test
    @DisplayName("GET /api/v1/turmas: Deve retornar 200 com lista de turmas")
    void deveListarTurmasComSucesso() {
        resource.turmaService = new TurmaService() {
            @Override
            public List<TurmaResponseDTO> listarPaginado(int pagina, int tamanho, String professor) {
                return List.of(mockDTO);
            }
        };

        Response response = resource.listarTurmas(0, 10, null);

        assertEquals(200, response.getStatus());
        assertEquals(List.of(mockDTO), response.getEntity());
    }

    @Test
    @DisplayName("GET /api/v1/turmas/{id}: Deve retornar 200 quando encontrada")
    void deveBuscarPorIdExistente() {
        resource.turmaService = new TurmaService() {
            @Override
            public Optional<TurmaResponseDTO> buscarPorId(Long id) {
                return Optional.of(mockDTO);
            }
        };

        Response response = resource.buscarTurmaPorId(1L);

        assertEquals(200, response.getStatus());
        assertEquals(mockDTO, response.getEntity());
    }

    @Test
    @DisplayName("GET /api/v1/turmas/{id}: Deve retornar 404 quando não encontrada")
    void deveRetornar404QuandoNaoEncontrada() {
        resource.turmaService = new TurmaService() {
            @Override
            public Optional<TurmaResponseDTO> buscarPorId(Long id) {
                return Optional.empty();
            }
        };

        Response response = resource.buscarTurmaPorId(999L);

        assertEquals(404, response.getStatus());
    }

    @Test
    @DisplayName("PUT /api/v1/turmas/{id}: Deve retornar 200 ao atualizar com sucesso")
    void deveAtualizarComSucesso() {
        resource.turmaService = new TurmaService() {
            @Override
            public Optional<TurmaResponseDTO> atualizar(Long id, TurmaRequestDTO dto) {
                return Optional.of(mockDTO);
            }
        };

        TurmaRequestDTO request = new TurmaRequestDTO("LOG-01", "Prof. Aristóteles", "Descrição");
        Response response = resource.atualizarTurma(1L, request);

        assertEquals(200, response.getStatus());
        assertEquals(mockDTO, response.getEntity());
    }

    @Test
    @DisplayName("PUT /api/v1/turmas/{id}: Deve retornar 404 ao atualizar turma inexistente")
    void deveRetornar404AoAtualizarInexistente() {
        resource.turmaService = new TurmaService() {
            @Override
            public Optional<TurmaResponseDTO> atualizar(Long id, TurmaRequestDTO dto) {
                return Optional.empty();
            }
        };

        TurmaRequestDTO request = new TurmaRequestDTO("LOG-01", "Prof. Aristóteles", "Descrição");
        Response response = resource.atualizarTurma(999L, request);

        assertEquals(404, response.getStatus());
    }

    @Test
    @DisplayName("DELETE /api/v1/turmas/{id}: Deve retornar 204 ao remover com sucesso")
    void deveDeletarComSucesso() {
        resource.turmaService = new TurmaService() {
            @Override
            public boolean deletar(Long id) {
                return true;
            }
        };

        Response response = resource.deletarTurma(1L);

        assertEquals(204, response.getStatus());
    }

    @Test
    @DisplayName("DELETE /api/v1/turmas/{id}: Deve retornar 404 ao tentar remover inexistente")
    void deveRetornar404AoDeletarInexistente() {
        resource.turmaService = new TurmaService() {
            @Override
            public boolean deletar(Long id) {
                return false;
            }
        };

        Response response = resource.deletarTurma(999L);

        assertEquals(404, response.getStatus());
    }
}
