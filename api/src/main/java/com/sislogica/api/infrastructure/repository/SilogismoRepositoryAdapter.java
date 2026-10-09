package com.sislogica.api.infrastructure.repository;

import com.sislogica.api.domain.model.Silogismo;
import com.sislogica.api.domain.repository.SilogismoRepositoryPort;
import com.sislogica.api.infrastructure.entity.SilogismoPanacheEntity;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SilogismoRepositoryAdapter implements SilogismoRepositoryPort {

    @Override
    public Silogismo salvar(Silogismo silogismo) {
        SilogismoPanacheEntity entity;
        if (silogismo.getId() != null) {
            entity = SilogismoPanacheEntity.findById(silogismo.getId());
            if (entity == null) {
                entity = new SilogismoPanacheEntity();
            }
        } else {
            entity = new SilogismoPanacheEntity();
        }

        entity.titulo = silogismo.getTitulo();
        entity.premissaMaior = silogismo.getPremissaMaior();
        entity.premissaMenor = silogismo.getPremissaMenor();
        entity.conclusao = silogismo.getConclusao();
        entity.modo = silogismo.getModo();
        entity.turmaId = silogismo.getTurmaId();
        entity.valido = silogismo.isValido();
        if (silogismo.getCriadoEm() != null) {
            entity.criadoEm = silogismo.getCriadoEm();
        }

        entity.persist();
        return toDomain(entity);
    }

    @Override
    public Optional<Silogismo> buscarPorId(Long id) {
        return SilogismoPanacheEntity.<SilogismoPanacheEntity>findByIdOptional(id)
                .map(this::toDomain);
    }

    @Override
    public List<Silogismo> listarPorTurma(Long turmaId, int pagina, int tamanho) {
        return SilogismoPanacheEntity.<SilogismoPanacheEntity>find("turmaId", turmaId)
                .page(Page.of(pagina, tamanho))
                .list()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Silogismo> listar(int pagina, int tamanho, String modo) {
        if (modo != null && !modo.isBlank()) {
            return SilogismoPanacheEntity.<SilogismoPanacheEntity>find("modo", modo)
                    .page(Page.of(pagina, tamanho))
                    .list()
                    .stream()
                    .map(this::toDomain)
                    .toList();
        }
        return SilogismoPanacheEntity.<SilogismoPanacheEntity>findAll()
                .page(Page.of(pagina, tamanho))
                .list()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean deletarPorId(Long id) {
        return SilogismoPanacheEntity.deleteById(id);
    }

    private Silogismo toDomain(SilogismoPanacheEntity entity) {
        return new Silogismo(
                entity.id,
                entity.titulo,
                entity.premissaMaior,
                entity.premissaMenor,
                entity.conclusao,
                entity.modo,
                entity.turmaId,
                entity.valido,
                entity.criadoEm
        );
    }
}
