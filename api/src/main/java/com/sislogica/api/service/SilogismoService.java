package com.sislogica.api.service;

import com.sislogica.api.domain.model.Silogismo;
import com.sislogica.api.domain.repository.SilogismoRepositoryPort;
import com.sislogica.api.dto.SilogismoRequestDTO;
import com.sislogica.api.dto.SilogismoResponseDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class SilogismoService {

    @Inject
    public SilogismoRepositoryPort silogismoRepository;

    public SilogismoService() {
    }

    public SilogismoService(SilogismoRepositoryPort silogismoRepository) {
        this.silogismoRepository = silogismoRepository;
    }

    @Transactional
    public SilogismoResponseDTO criar(SilogismoRequestDTO dto) {
        Silogismo domain = new Silogismo(
                null,
                dto.titulo,
                dto.premissaMaior,
                dto.premissaMenor,
                dto.conclusao,
                dto.modo,
                dto.turmaId,
                true,
                Instant.now()
        );
        Silogismo salvo = silogismoRepository.salvar(domain);
        return SilogismoResponseDTO.fromDomain(salvo);
    }

    public Optional<SilogismoResponseDTO> buscarPorId(Long id) {
        return silogismoRepository.buscarPorId(id)
                .map(SilogismoResponseDTO::fromDomain);
    }

    public List<SilogismoResponseDTO> listarPaginado(int pagina, int tamanho, String modo) {
        return silogismoRepository.listar(pagina, tamanho, modo)
                .stream()
                .map(SilogismoResponseDTO::fromDomain)
                .toList();
    }

    @Transactional
    public Optional<SilogismoResponseDTO> atualizar(Long id, SilogismoRequestDTO dto) {
        Optional<Silogismo> existente = silogismoRepository.buscarPorId(id);
        if (existente.isEmpty()) {
            return Optional.empty();
        }

        Silogismo domain = existente.get();
        domain.setTitulo(dto.titulo);
        domain.setPremissaMaior(dto.premissaMaior);
        domain.setPremissaMenor(dto.premissaMenor);
        domain.setConclusao(dto.conclusao);
        domain.setModo(dto.modo);
        domain.setTurmaId(dto.turmaId);

        Silogismo atualizado = silogismoRepository.salvar(domain);
        return Optional.of(SilogismoResponseDTO.fromDomain(atualizado));
    }

    @Transactional
    public boolean deletar(Long id) {
        return silogismoRepository.deletarPorId(id);
    }

    public List<SilogismoResponseDTO> buscarPorTurmaPaginado(Long turmaId, int pagina, int tamanho) {
        return silogismoRepository.listarPorTurma(turmaId, pagina, tamanho)
                .stream()
                .map(SilogismoResponseDTO::fromDomain)
                .toList();
    }
}
