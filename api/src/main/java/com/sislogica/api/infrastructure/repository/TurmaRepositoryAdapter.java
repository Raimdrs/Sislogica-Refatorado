package com.sislogica.api.infrastructure.repository;

import com.sislogica.api.domain.model.Turma;
import com.sislogica.api.domain.repository.TurmaRepositoryPort;
import com.sislogica.api.infrastructure.entity.TurmaPanacheEntity;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TurmaRepositoryAdapter implements TurmaRepositoryPort {

    @Override
    public Turma salvar(Turma turma) {
        TurmaPanacheEntity entity;
        if (turma.getId() != null) {
            entity = TurmaPanacheEntity.findById(turma.getId());
            if (entity == null) {
                throw new IllegalArgumentException("Turma com ID " + turma.getId() + " não encontrada para atualização.");
            }
        } else {
            entity = new TurmaPanacheEntity();
        }

        entity.codigo = turma.getCodigo();
        entity.professor = turma.getProfessor();
        entity.descricao = turma.getDescricao();
        if (turma.getCriadoEm() != null) {
            entity.criadoEm = turma.getCriadoEm();
        }

        entity.persist();
        return toDomain(entity);
    }

    @Override
    public Optional<Turma> buscarPorId(Long id) {
        return TurmaPanacheEntity.<TurmaPanacheEntity>findByIdOptional(id)
                .map(this::toDomain);
    }

    @Override
    public Optional<Turma> buscarPorCodigo(String codigo) {
        return TurmaPanacheEntity.<TurmaPanacheEntity>find("codigo", codigo)
                .firstResultOptional()
                .map(this::toDomain);
    }

    @Override
    public List<Turma> listar(int pagina, int tamanho, String professor) {
        if (professor != null && !professor.isBlank()) {
            return TurmaPanacheEntity.<TurmaPanacheEntity>find("LOWER(professor) LIKE LOWER(?1)", "%" + professor + "%")
                    .page(Page.of(pagina, tamanho))
                    .list()
                    .stream()
                    .map(this::toDomain)
                    .toList();
        }
        return TurmaPanacheEntity.<TurmaPanacheEntity>findAll()
                .page(Page.of(pagina, tamanho))
                .list()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean deletarPorId(Long id) {
        return TurmaPanacheEntity.deleteById(id);
    }

    private Turma toDomain(TurmaPanacheEntity entity) {
        return new Turma(
                entity.id,
                entity.codigo,
                entity.professor,
                entity.descricao,
                entity.criadoEm
        );
    }
}
