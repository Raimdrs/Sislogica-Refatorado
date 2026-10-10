package com.sislogica.api.service;

import com.sislogica.api.domain.model.Turma;
import com.sislogica.api.domain.repository.TurmaRepositoryPort;
import com.sislogica.api.dto.TurmaRequestDTO;
import com.sislogica.api.dto.TurmaResponseDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TurmaService {

    @Inject
    public TurmaRepositoryPort turmaRepository;

    public TurmaService() {
    }

    public TurmaService(TurmaRepositoryPort turmaRepository) {
        this.turmaRepository = turmaRepository;
    }

    @Transactional
    public TurmaResponseDTO criar(TurmaRequestDTO dto) {
        if (turmaRepository.buscarPorCodigo(dto.codigo()).isPresent()) {
            throw new IllegalArgumentException("Já existe uma turma cadastrada com o código informado: " + dto.codigo());
        }

        Turma domain = new Turma(
                null,
                dto.codigo(),
                dto.professor(),
                dto.descricao(),
                Instant.now()
        );

        Turma salva = turmaRepository.salvar(domain);
        return TurmaResponseDTO.fromDomain(salva);
    }

    public Optional<TurmaResponseDTO> buscarPorId(Long id) {
        return turmaRepository.buscarPorId(id)
                .map(TurmaResponseDTO::fromDomain);
    }

    public Optional<TurmaResponseDTO> buscarPorCodigo(String codigo) {
        return turmaRepository.buscarPorCodigo(codigo)
                .map(TurmaResponseDTO::fromDomain);
    }

    public List<TurmaResponseDTO> listarPaginado(int pagina, int tamanho, String professor) {
        return turmaRepository.listar(pagina, tamanho, professor)
                .stream()
                .map(TurmaResponseDTO::fromDomain)
                .toList();
    }

    @Transactional
    public Optional<TurmaResponseDTO> atualizar(Long id, TurmaRequestDTO dto) {
        Optional<Turma> existente = turmaRepository.buscarPorId(id);
        if (existente.isEmpty()) {
            return Optional.empty();
        }

        Turma domain = existente.get();
        if (!domain.getCodigo().equals(dto.codigo())) {
            Optional<Turma> comMesmoCodigo = turmaRepository.buscarPorCodigo(dto.codigo());
            if (comMesmoCodigo.isPresent()) {
                throw new IllegalArgumentException("Já existe outra turma com o código informado: " + dto.codigo());
            }
        }

        domain.setCodigo(dto.codigo());
        domain.setProfessor(dto.professor());
        domain.setDescricao(dto.descricao());

        Turma atualizada = turmaRepository.salvar(domain);
        return Optional.of(TurmaResponseDTO.fromDomain(atualizada));
    }

    @Transactional
    public boolean deletar(Long id) {
        return turmaRepository.deletarPorId(id);
    }
}
