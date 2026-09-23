package com.sislogica.api.domain.repository;

import com.sislogica.api.domain.model.Silogismo;
import java.util.List;
import java.util.Optional;

/**
 * Porta de saída (Output Port) para operações de persistência de Silogismos.
 * Definida no domínio, implementada pelos adaptadores de infraestrutura.
 */
public interface SilogismoRepositoryPort {

    Silogismo salvar(Silogismo silogismo);

    Optional<Silogismo> buscarPorId(Long id);

    List<Silogismo> listarPorTurma(Long turmaId, int pagina, int tamanho);

    List<Silogismo> listar(int pagina, int tamanho, String modo);

    boolean deletarPorId(Long id);
}

