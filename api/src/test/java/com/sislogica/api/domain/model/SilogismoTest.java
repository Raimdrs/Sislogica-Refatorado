package com.sislogica.api.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SilogismoTest {

    @Test
    @DisplayName("Deve instanciar um Silogismo com SilogismoDetalhe vinculado 1:1")
    void deveInstanciarSilogismoComDetalheCorretamente() {
        Instant agora = Instant.now();
        Proposicao premissaMaior = new Proposicao(Quantificador.A, Termo.M, Termo.T, "Todo homem é mortal");
        Proposicao premissaMenor = new Proposicao(Quantificador.A, Termo.t, Termo.M, "Sócrates é homem");
        Proposicao conclusao = new Proposicao(Quantificador.A, Termo.t, Termo.T, "Sócrates é mortal");
        SilogismoDetalhe detalhe = new SilogismoDetalhe(1L, "AAA-1", "Barbara", true, false, null);

        Silogismo silogismo = new Silogismo(
            1L,
            "Silogismo Barbara",
            premissaMaior,
            premissaMenor,
            conclusao,
            10L,
            detalhe,
            agora
        );

        assertEquals(1L, silogismo.getId());
        assertEquals("Silogismo Barbara", silogismo.getTitulo());
        assertEquals(premissaMaior, silogismo.getPremissaMaior());
        assertEquals(premissaMenor, silogismo.getPremissaMenor());
        assertEquals(conclusao, silogismo.getConclusao());
        assertEquals(10L, silogismo.getTurmaId());
        assertNotNull(silogismo.getDetalhe());
        assertEquals("AAA-1", silogismo.getDetalhe().getModo());
        assertEquals("Barbara", silogismo.getDetalhe().getNome());
        assertTrue(silogismo.getDetalhe().isValido());
        assertEquals(agora, silogismo.getCriadoEm());
    }

    @Test
    @DisplayName("Deve instanciar Silogismo sem SilogismoDetalhe (nulo) e inicializar criadoEm se nulo")
    void deveInicializarCriadoEmSeNuloESemDetalhe() {
        Silogismo silogismo = new Silogismo(
            2L,
            "Silogismo Celarent",
            new Proposicao(Quantificador.E, Termo.M, Termo.T, "Nenhum réptil tem pelos"),
            new Proposicao(Quantificador.A, Termo.t, Termo.M, "Toda cobra é réptil"),
            new Proposicao(Quantificador.E, Termo.t, Termo.T, "Nenhuma cobra tem pelos"),
            10L,
            null,
            null
        );

        assertNull(silogismo.getDetalhe());
        assertNotNull(silogismo.getCriadoEm());
    }
}
