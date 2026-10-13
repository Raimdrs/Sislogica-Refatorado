package com.sislogica.api.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SilogismoTest {

    @Test
    @DisplayName("Deve instanciar um Silogismo com todos os atributos válidos")
    void deveInstanciarSilogismoCorretamente() {
        Instant agora = Instant.now();
        Proposicao premissaMaior = new Proposicao(Quantificador.A, Termo.M, Termo.T, "Todo homem é mortal");
        Proposicao premissaMenor = new Proposicao(Quantificador.A, Termo.t, Termo.M, "Sócrates é homem");
        Proposicao conclusao = new Proposicao(Quantificador.A, Termo.t, Termo.T, "Sócrates é mortal");

        Silogismo silogismo = new Silogismo(
            1L,
            "Silogismo Barbara",
            premissaMaior,
            premissaMenor,
            conclusao,
            "BARBARA",
            10L,
            true,
            agora
        );

        assertEquals(1L, silogismo.getId());
        assertEquals("Silogismo Barbara", silogismo.getTitulo());
        assertEquals(premissaMaior, silogismo.getPremissaMaior());
        assertEquals(premissaMenor, silogismo.getPremissaMenor());
        assertEquals(conclusao, silogismo.getConclusao());
        assertEquals("BARBARA", silogismo.getModo());
        assertEquals(10L, silogismo.getTurmaId());
        assertTrue(silogismo.isValido());
        assertEquals(agora, silogismo.getCriadoEm());
    }

    @Test
    @DisplayName("Deve inicializar criadoEm automaticamente se for passado nulo")
    void deveInicializarCriadoEmSeNulo() {
        Silogismo silogismo = new Silogismo(
            2L,
            "Silogismo Celarent",
            new Proposicao(Quantificador.E, Termo.M, Termo.T, "Nenhum réptil tem pelos"),
            new Proposicao(Quantificador.A, Termo.t, Termo.M, "Toda cobra é réptil"),
            new Proposicao(Quantificador.E, Termo.t, Termo.T, "Nenhuma cobra tem pelos"),
            "CELARENT",
            10L,
            true,
            null
        );

        assertNotNull(silogismo.getCriadoEm());
    }
}
