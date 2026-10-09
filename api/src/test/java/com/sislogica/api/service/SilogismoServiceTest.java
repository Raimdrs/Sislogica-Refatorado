package com.sislogica.api.service;

import com.sislogica.api.domain.model.Silogismo;
import com.sislogica.api.domain.repository.SilogismoRepositoryPort;
import com.sislogica.api.dto.SilogismoRequestDTO;
import com.sislogica.api.dto.SilogismoResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SilogismoServiceTest {

    private SilogismoService service;
    private Silogismo domainModel;

    @BeforeEach
    void setUp() {
        domainModel = new Silogismo(
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
    @DisplayName("Deve criar silogismo com sucesso")
    void deveCriarSilogismoComSucesso() {
        SilogismoRepositoryPort fakeRepo = new SilogismoRepositoryPort() {
            @Override
            public Silogismo salvar(Silogismo s) {
                s.setId(10L);
                return s;
            }

            @Override
            public Optional<Silogismo> buscarPorId(Long id) {
                return Optional.empty();
            }

            @Override
            public List<Silogismo> listarPorTurma(Long turmaId, int pagina, int tamanho) {
                return List.of();
            }

            @Override
            public List<Silogismo> listar(int pagina, int tamanho, String modo) {
                return List.of();
            }

            @Override
            public boolean deletarPorId(Long id) {
                return false;
            }
        };

        service = new SilogismoService(fakeRepo);

        SilogismoRequestDTO requestDTO = new SilogismoRequestDTO(
                "Barbara Teste",
                "Todo M é P",
                "Todo S é M",
                "Todo S é P",
                "BARBARA",
                1L
        );

        SilogismoResponseDTO responseDTO = service.criar(requestDTO);

        assertNotNull(responseDTO);
        assertEquals(10L, responseDTO.id);
        assertEquals("Barbara Teste", responseDTO.titulo);
        assertTrue(responseDTO.valido);
    }

    @Test
    @DisplayName("Deve buscar silogismo por id existente")
    void deveBuscarSilogismoPorIdExistente() {
        SilogismoRepositoryPort fakeRepo = new SilogismoRepositoryPort() {
            @Override
            public Silogismo salvar(Silogismo s) {
                return s;
            }

            @Override
            public Optional<Silogismo> buscarPorId(Long id) {
                return Optional.of(domainModel);
            }

            @Override
            public List<Silogismo> listarPorTurma(Long turmaId, int pagina, int tamanho) {
                return List.of();
            }

            @Override
            public List<Silogismo> listar(int pagina, int tamanho, String modo) {
                return List.of();
            }

            @Override
            public boolean deletarPorId(Long id) {
                return false;
            }
        };

        service = new SilogismoService(fakeRepo);

        Optional<SilogismoResponseDTO> opt = service.buscarPorId(1L);

        assertTrue(opt.isPresent());
        assertEquals("Barbara Teste", opt.get().titulo);
    }

    @Test
    @DisplayName("Deve listar silogismos paginados")
    void deveListarSilogismosPaginados() {
        SilogismoRepositoryPort fakeRepo = new SilogismoRepositoryPort() {
            @Override
            public Silogismo salvar(Silogismo s) {
                return s;
            }

            @Override
            public Optional<Silogismo> buscarPorId(Long id) {
                return Optional.empty();
            }

            @Override
            public List<Silogismo> listarPorTurma(Long turmaId, int pagina, int tamanho) {
                return List.of();
            }

            @Override
            public List<Silogismo> listar(int pagina, int tamanho, String modo) {
                return List.of(domainModel);
            }

            @Override
            public boolean deletarPorId(Long id) {
                return false;
            }
        };

        service = new SilogismoService(fakeRepo);

        List<SilogismoResponseDTO> lista = service.listarPaginado(0, 10, "BARBARA");

        assertEquals(1, lista.size());
        assertEquals("BARBARA", lista.getFirst().modo);
    }

    @Test
    @DisplayName("Deve atualizar silogismo existente com sucesso")
    void deveAtualizarSilogismoComSucesso() {
        SilogismoRepositoryPort fakeRepo = new SilogismoRepositoryPort() {
            @Override
            public Silogismo salvar(Silogismo s) {
                return s;
            }

            @Override
            public Optional<Silogismo> buscarPorId(Long id) {
                return Optional.of(domainModel);
            }

            @Override
            public List<Silogismo> listarPorTurma(Long turmaId, int pagina, int tamanho) {
                return List.of();
            }

            @Override
            public List<Silogismo> listar(int pagina, int tamanho, String modo) {
                return List.of();
            }

            @Override
            public boolean deletarPorId(Long id) {
                return false;
            }
        };

        service = new SilogismoService(fakeRepo);

        SilogismoRequestDTO dto = new SilogismoRequestDTO(
                "Barbara Atualizado",
                "Todo M é P",
                "Todo S é M",
                "Todo S é P",
                "BARBARA",
                1L
        );

        Optional<SilogismoResponseDTO> atualizado = service.atualizar(1L, dto);

        assertTrue(atualizado.isPresent());
        assertEquals("Barbara Atualizado", atualizado.get().titulo);
    }

    @Test
    @DisplayName("Deve retornar vazio ao atualizar silogismo inexistente")
    void deveRetornarVazioAoAtualizarInexistente() {
        SilogismoRepositoryPort fakeRepo = new SilogismoRepositoryPort() {
            @Override
            public Silogismo salvar(Silogismo s) {
                return s;
            }

            @Override
            public Optional<Silogismo> buscarPorId(Long id) {
                return Optional.empty();
            }

            @Override
            public List<Silogismo> listarPorTurma(Long turmaId, int pagina, int tamanho) {
                return List.of();
            }

            @Override
            public List<Silogismo> listar(int pagina, int tamanho, String modo) {
                return List.of();
            }

            @Override
            public boolean deletarPorId(Long id) {
                return false;
            }
        };

        service = new SilogismoService(fakeRepo);

        SilogismoRequestDTO dto = new SilogismoRequestDTO(
                "Barbara Atualizado",
                "Todo M é P",
                "Todo S é M",
                "Todo S é P",
                "BARBARA",
                1L
        );

        Optional<SilogismoResponseDTO> atualizado = service.atualizar(999L, dto);

        assertTrue(atualizado.isEmpty());
    }

    @Test
    @DisplayName("Deve deletar silogismo por id")
    void deveDeletarSilogismoPorId() {
        SilogismoRepositoryPort fakeRepo = new SilogismoRepositoryPort() {
            @Override
            public Silogismo salvar(Silogismo s) {
                return s;
            }

            @Override
            public Optional<Silogismo> buscarPorId(Long id) {
                return Optional.empty();
            }

            @Override
            public List<Silogismo> listarPorTurma(Long turmaId, int pagina, int tamanho) {
                return List.of();
            }

            @Override
            public List<Silogismo> listar(int pagina, int tamanho, String modo) {
                return List.of();
            }

            @Override
            public boolean deletarPorId(Long id) {
                return id.equals(1L);
            }
        };

        service = new SilogismoService(fakeRepo);

        assertTrue(service.deletar(1L));
        assertFalse(service.deletar(999L));
    }
}
