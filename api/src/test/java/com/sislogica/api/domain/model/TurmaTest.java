package com.sislogica.api.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TurmaTest {

    @Test
    @DisplayName("Deve instanciar Turma com todos os atributos via construtor")
    void deveInstanciarTurmaViaConstrutor() {
        Instant agora = Instant.now();
        Turma turma = new Turma(1L, "LOGICA-2026-1", "Prof. Aristóteles", "Turma de Lógica Clássica", agora);

        assertEquals(1L, turma.getId());
        assertEquals("LOGICA-2026-1", turma.getCodigo());
        assertEquals("Prof. Aristóteles", turma.getProfessor());
        assertEquals("Turma de Lógica Clássica", turma.getDescricao());
        assertEquals(agora, turma.getCriadoEm());
    }

    @Test
    @DisplayName("Deve permitir alterar atributos via setters")
    void deveAlterarAtributosViaSetters() {
        Turma turma = new Turma();
        turma.setId(2L);
        turma.setCodigo("LOGICA-2026-2");
        turma.setProfessor("Prof. Platão");
        turma.setDescricao("Introdução à dialética");
        Instant agora = Instant.now();
        turma.setCriadoEm(agora);

        assertEquals(2L, turma.getId());
        assertEquals("LOGICA-2026-2", turma.getCodigo());
        assertEquals("Prof. Platão", turma.getProfessor());
        assertEquals("Introdução à dialética", turma.getDescricao());
        assertEquals(agora, turma.getCriadoEm());
    }
}
