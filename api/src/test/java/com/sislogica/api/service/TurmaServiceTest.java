package com.sislogica.api.service;

import com.sislogica.api.domain.model.Turma;
import com.sislogica.api.domain.repository.TurmaRepositoryPort;
import com.sislogica.api.dto.TurmaRequestDTO;
import com.sislogica.api.dto.TurmaResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TurmaServiceTest {

    private TurmaService service;
    private Turma domainModel;

    @BeforeEach
    void setUp() {
        domainModel = new Turma(1L, "LOG-01", "Prof. Aristóteles", "Turma Inicial", Instant.now());
    }

    @Test
    @DisplayName("Deve criar turma com sucesso")
    void deveCriarTurmaComSucesso() {
        TurmaRepositoryPort fakeRepo = new TurmaRepositoryPort() {
            @Override
            public Turma salvar(Turma turma) {
                turma.setId(10L);
                return turma;
            }

            @Override
            public Optional<Turma> buscarPorId(Long id) {
                return Optional.empty();
            }

            @Override
            public Optional<Turma> buscarPorCodigo(String codigo) {
                return Optional.empty();
            }

            @Override
            public List<Turma> listar(int pagina, int tamanho, String professor) {
                return List.of();
            }

            @Override
            public boolean deletarPorId(Long id) {
                return false;
            }
        };

        service = new TurmaService(fakeRepo);

        TurmaRequestDTO dto = new TurmaRequestDTO("LOG-01", "Prof. Aristóteles", "Descrição");
        TurmaResponseDTO res = service.criar(dto);

        assertNotNull(res);
        assertEquals(10L, res.id());
        assertEquals("LOG-01", res.codigo());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar turma com código duplicado")
    void deveLancarExcecaoAoCriarComCodigoDuplicado() {
        TurmaRepositoryPort fakeRepo = new TurmaRepositoryPort() {
            @Override
            public Turma salvar(Turma turma) {
                return turma;
            }

            @Override
            public Optional<Turma> buscarPorId(Long id) {
                return Optional.empty();
            }

            @Override
            public Optional<Turma> buscarPorCodigo(String codigo) {
                return Optional.of(domainModel);
            }

            @Override
            public List<Turma> listar(int pagina, int tamanho, String professor) {
                return List.of();
            }

            @Override
            public boolean deletarPorId(Long id) {
                return false;
            }
        };

        service = new TurmaService(fakeRepo);

        TurmaRequestDTO dto = new TurmaRequestDTO("LOG-01", "Prof. Aristóteles", "Descrição");
        assertThrows(IllegalArgumentException.class, () -> service.criar(dto));
    }

    @Test
    @DisplayName("Deve buscar turma por id existente")
    void deveBuscarPorIdExistente() {
        TurmaRepositoryPort fakeRepo = new TurmaRepositoryPort() {
            @Override
            public Turma salvar(Turma turma) {
                return turma;
            }

            @Override
            public Optional<Turma> buscarPorId(Long id) {
                return Optional.of(domainModel);
            }

            @Override
            public Optional<Turma> buscarPorCodigo(String codigo) {
                return Optional.empty();
            }

            @Override
            public List<Turma> listar(int pagina, int tamanho, String professor) {
                return List.of();
            }

            @Override
            public boolean deletarPorId(Long id) {
                return false;
            }
        };

        service = new TurmaService(fakeRepo);

        Optional<TurmaResponseDTO> res = service.buscarPorId(1L);
        assertTrue(res.isPresent());
        assertEquals("LOG-01", res.get().codigo());
    }

    @Test
    @DisplayName("Deve listar turmas paginadas")
    void deveListarPaginado() {
        TurmaRepositoryPort fakeRepo = new TurmaRepositoryPort() {
            @Override
            public Turma salvar(Turma turma) {
                return turma;
            }

            @Override
            public Optional<Turma> buscarPorId(Long id) {
                return Optional.empty();
            }

            @Override
            public Optional<Turma> buscarPorCodigo(String codigo) {
                return Optional.empty();
            }

            @Override
            public List<Turma> listar(int pagina, int tamanho, String professor) {
                return List.of(domainModel);
            }

            @Override
            public boolean deletarPorId(Long id) {
                return false;
            }
        };

        service = new TurmaService(fakeRepo);

        List<TurmaResponseDTO> lista = service.listarPaginado(0, 10, null);
        assertEquals(1, lista.size());
        assertEquals("LOG-01", lista.getFirst().codigo());
    }

    @Test
    @DisplayName("Deve atualizar turma existente com sucesso")
    void deveAtualizarComSucesso() {
        TurmaRepositoryPort fakeRepo = new TurmaRepositoryPort() {
            @Override
            public Turma salvar(Turma turma) {
                return turma;
            }

            @Override
            public Optional<Turma> buscarPorId(Long id) {
                return Optional.of(domainModel);
            }

            @Override
            public Optional<Turma> buscarPorCodigo(String codigo) {
                return Optional.empty();
            }

            @Override
            public List<Turma> listar(int pagina, int tamanho, String professor) {
                return List.of();
            }

            @Override
            public boolean deletarPorId(Long id) {
                return false;
            }
        };

        service = new TurmaService(fakeRepo);

        TurmaRequestDTO dto = new TurmaRequestDTO("LOG-02", "Prof. Novo", "Nova descrição");
        Optional<TurmaResponseDTO> res = service.atualizar(1L, dto);

        assertTrue(res.isPresent());
        assertEquals("LOG-02", res.get().codigo());
        assertEquals("Prof. Novo", res.get().professor());
    }

    @Test
    @DisplayName("Deve retornar vazio ao atualizar turma inexistente")
    void deveRetornarVazioAoAtualizarInexistente() {
        TurmaRepositoryPort fakeRepo = new TurmaRepositoryPort() {
            @Override
            public Turma salvar(Turma turma) {
                return turma;
            }

            @Override
            public Optional<Turma> buscarPorId(Long id) {
                return Optional.empty();
            }

            @Override
            public Optional<Turma> buscarPorCodigo(String codigo) {
                return Optional.empty();
            }

            @Override
            public List<Turma> listar(int pagina, int tamanho, String professor) {
                return List.of();
            }

            @Override
            public boolean deletarPorId(Long id) {
                return false;
            }
        };

        service = new TurmaService(fakeRepo);

        TurmaRequestDTO dto = new TurmaRequestDTO("LOG-02", "Prof. Novo", "Nova descrição");
        Optional<TurmaResponseDTO> res = service.atualizar(999L, dto);

        assertTrue(res.isEmpty());
    }

    @Test
    @DisplayName("Deve deletar turma por id")
    void deveDeletarPorId() {
        TurmaRepositoryPort fakeRepo = new TurmaRepositoryPort() {
            @Override
            public Turma salvar(Turma turma) {
                return turma;
            }

            @Override
            public Optional<Turma> buscarPorId(Long id) {
                return Optional.empty();
            }

            @Override
            public Optional<Turma> buscarPorCodigo(String codigo) {
                return Optional.empty();
            }

            @Override
            public List<Turma> listar(int pagina, int tamanho, String professor) {
                return List.of();
            }

            @Override
            public boolean deletarPorId(Long id) {
                return id.equals(1L);
            }
        };

        service = new TurmaService(fakeRepo);

        assertTrue(service.deletar(1L));
        assertFalse(service.deletar(999L));
    }
}
