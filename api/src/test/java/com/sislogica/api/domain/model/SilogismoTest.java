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
        Silogismo silogismo = new Silogismo(
            1L,
            "Silogismo Barbara",
            "Todo homem é mortal",
            "Sócrates é homem",
            "Sócrates é mortal",
            "BARBARA",
            10L,
            true,
            agora
        );

        assertEquals(1L, silogismo.getId());
        assertEquals("Silogismo Barbara", silogismo.getTitulo());
        assertEquals("Todo homem é mortal", silogismo.getPremissaMaior());
        assertEquals("Sócrates é homem", silogismo.getPremissaMenor());
        assertEquals("Sócrates é mortal", silogismo.getConclusao());
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
            "Nenhum réptil tem pelos",
            "Toda cobra é réptil",
            "Nenhuma cobra tem pelos",
            "CELARENT",
            10L,
            true,
            null
        );

        assertNotNull(silogismo.getCriadoEm());
    }
}
