package com.sislogica.api.infrastructure.repository;

import com.sislogica.api.domain.model.Proposicao;
import com.sislogica.api.domain.model.Reducao;
import com.sislogica.api.domain.model.Silogismo;
import com.sislogica.api.domain.model.SilogismoDetalhe;
import com.sislogica.api.domain.repository.SilogismoRepositoryPort;
import com.sislogica.api.infrastructure.entity.ProposicaoPanacheEntity;
import com.sislogica.api.infrastructure.entity.ReducaoPanacheEntity;
import com.sislogica.api.infrastructure.entity.SilogismoDetalhePanacheEntity;
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
                throw new IllegalArgumentException("Silogismo com ID " + silogismo.getId() + " não encontrado para atualização.");
            }
        } else {
            entity = new SilogismoPanacheEntity();
        }

        entity.titulo = silogismo.getTitulo();
        entity.premissaMaior = toProposicaoEntity(entity.premissaMaior, silogismo.getPremissaMaior());
        entity.premissaMenor = toProposicaoEntity(entity.premissaMenor, silogismo.getPremissaMenor());
        entity.conclusao = toProposicaoEntity(entity.conclusao, silogismo.getConclusao());
        entity.turmaId = silogismo.getTurmaId();
        entity.detalhe = toDetalheEntity(entity.detalhe, silogismo.getDetalhe());
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
            return SilogismoPanacheEntity.<SilogismoPanacheEntity>find("detalhe.modo", modo)
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

    private ProposicaoPanacheEntity toProposicaoEntity(ProposicaoPanacheEntity existing, Proposicao domain) {
        if (domain == null) {
            return null;
        }
        ProposicaoPanacheEntity propEntity = existing != null ? existing : new ProposicaoPanacheEntity();
        propEntity.quantificador = domain.getQuantificador();
        propEntity.sujeito = domain.getSujeito();
        propEntity.predicado = domain.getPredicado();
        propEntity.transliteracao = domain.getTransliteracao();
        return propEntity;
    }

    private Proposicao toProposicaoDomain(ProposicaoPanacheEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Proposicao(
                entity.id,
                entity.quantificador,
                entity.sujeito,
                entity.predicado,
                entity.transliteracao
        );
    }

    private ReducaoPanacheEntity toReducaoEntity(ReducaoPanacheEntity existing, Reducao domain) {
        if (domain == null) {
            return null;
        }
        ReducaoPanacheEntity reducaoEntity = existing != null ? existing : new ReducaoPanacheEntity();
        reducaoEntity.modo = domain.getModo();
        reducaoEntity.nome = domain.getNome();
        reducaoEntity.proposicaoMaior = toProposicaoEntity(reducaoEntity.proposicaoMaior, domain.getProposicaoMaior());
        reducaoEntity.proposicaoMenor = toProposicaoEntity(reducaoEntity.proposicaoMenor, domain.getProposicaoMenor());
        reducaoEntity.conclusao = toProposicaoEntity(reducaoEntity.conclusao, domain.getConclusao());
        return reducaoEntity;
    }

    private Reducao toReducaoDomain(ReducaoPanacheEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Reducao(
                entity.id,
                entity.modo,
                entity.nome,
                toProposicaoDomain(entity.proposicaoMaior),
                toProposicaoDomain(entity.proposicaoMenor),
                toProposicaoDomain(entity.conclusao)
        );
    }

    private SilogismoDetalhePanacheEntity toDetalheEntity(SilogismoDetalhePanacheEntity existing, SilogismoDetalhe domain) {
        if (domain == null) {
            return null;
        }
        SilogismoDetalhePanacheEntity detalheEntity = existing != null ? existing : new SilogismoDetalhePanacheEntity();
        detalheEntity.modo = domain.getModo();
        detalheEntity.nome = domain.getNome();
        detalheEntity.valido = domain.isValido();
        detalheEntity.reduzivel = domain.isReduzivel();
        detalheEntity.reducao = toReducaoEntity(detalheEntity.reducao, domain.getReducao());
        return detalheEntity;
    }

    private SilogismoDetalhe toDetalheDomain(SilogismoDetalhePanacheEntity entity) {
        if (entity == null) {
            return null;
        }
        return new SilogismoDetalhe(
                entity.id,
                entity.modo,
                entity.nome,
                entity.valido,
                entity.reduzivel,
                toReducaoDomain(entity.reducao)
        );
    }

    private Silogismo toDomain(SilogismoPanacheEntity entity) {
        return new Silogismo(
                entity.id,
                entity.titulo,
                toProposicaoDomain(entity.premissaMaior),
                toProposicaoDomain(entity.premissaMenor),
                toProposicaoDomain(entity.conclusao),
                entity.turmaId,
                toDetalheDomain(entity.detalhe),
                entity.criadoEm
        );
    }
}
