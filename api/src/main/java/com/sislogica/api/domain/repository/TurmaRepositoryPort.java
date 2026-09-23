package com.sislogica.api.domain.repository;

import com.sislogica.api.domain.model.Turma;
import java.util.List;
import java.util.Optional;

/**
 * Porta de saída (Output Port) para operações de persistência de Turmas.
 * Definida no domínio, implementada pelos adaptadores de infraestrutura.
 */
public interface TurmaRepositoryPort {

    Turma salvar(Turma turma);

    Optional<Turma> buscarPorId(Long id);

    Optional<Turma> buscarPorCodigo(String codigo);

    List<Turma> listar(int pagina, int tamanho, String professor);

    boolean deletarPorId(Long id);
}

