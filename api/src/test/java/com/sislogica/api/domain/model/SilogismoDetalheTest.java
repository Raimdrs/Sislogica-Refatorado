package com.sislogica.api.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SilogismoDetalheTest {

    @Test
    @DisplayName("Deve instanciar SilogismoDetalhe válido e não reduzível (ex.: 1ª figura AAA-1 Barbara)")
    void deveInstanciarDetalheNaoReduzivel() {
        SilogismoDetalhe detalhe = new SilogismoDetalhe(
                1L,
                "AAA-1",
                "Barbara",
                true,
                false,
                null
        );

        assertEquals(1L, detalhe.getId());
        assertEquals("AAA-1", detalhe.getModo());
        assertEquals("Barbara", detalhe.getNome());
        assertTrue(detalhe.isValido());
        assertFalse(detalhe.isReduzivel());
        assertNull(detalhe.getReducao());
    }

    @Test
    @DisplayName("Deve instanciar SilogismoDetalhe reduzível com Reducao obrigatória (ex.: EAE-2 Cesare -> EAE-1 Celarent)")
    void deveInstanciarDetalheReduzivelComReducao() {
        Reducao reducao = new Reducao(
                10L,
                "EAE-1",
                "Celarent",
                new Proposicao(Quantificador.E, Termo.M, Termo.T, "Nenhum M é T"),
                new Proposicao(Quantificador.A, Termo.t, Termo.M, "Todo t é M"),
                new Proposicao(Quantificador.E, Termo.t, Termo.T, "Nenhum t é T")
        );

        SilogismoDetalhe detalhe = new SilogismoDetalhe(
                2L,
                "EAE-2",
                "Cesare",
                true,
                true,
                reducao
        );

        assertEquals(2L, detalhe.getId());
        assertEquals("EAE-2", detalhe.getModo());
        assertEquals("Cesare", detalhe.getNome());
        assertTrue(detalhe.isValido());
        assertTrue(detalhe.isReduzivel());
        assertNotNull(detalhe.getReducao());
        assertEquals("EAE-1", detalhe.getReducao().getModo());
        assertEquals("Celarent", detalhe.getReducao().getNome());
    }

    @Test
    @DisplayName("Deve permitir modo e nome opcionais (nulos) em SilogismoDetalhe")
    void devePermitirModoENomeOpcionais() {
        SilogismoDetalhe detalhe = new SilogismoDetalhe(null, null, false, false, null);

        assertNull(detalhe.getModo());
        assertNull(detalhe.getNome());
        assertFalse(detalhe.isValido());
        assertFalse(detalhe.isReduzivel());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando reduzivel for true e reducao for nula")
    void deveExigirReducaoQuandoReduzivelForTrue() {
        assertThrows(IllegalArgumentException.class,
                () -> new SilogismoDetalhe("EAE-2", "Cesare", true, true, null));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando valido ou reduzivel forem nulos")
    void deveExigirValidoEReduzivel() {
        assertThrows(IllegalArgumentException.class,
                () -> new SilogismoDetalhe("AAA-1", "Barbara", null, false, null));
        assertThrows(IllegalArgumentException.class,
                () -> new SilogismoDetalhe("AAA-1", "Barbara", true, null, null));
    }

    @Test
    @DisplayName("Deve validar campos obrigatórios na entidade Reducao")
    void deveValidarCamposObrigatoriosDaReducao() {
        Proposicao pMaior = new Proposicao(Quantificador.E, Termo.M, Termo.T);
        Proposicao pMenor = new Proposicao(Quantificador.A, Termo.t, Termo.M);
        Proposicao conc = new Proposicao(Quantificador.E, Termo.t, Termo.T);

        assertThrows(IllegalArgumentException.class,
                () -> new Reducao(null, "Celarent", pMaior, pMenor, conc));
        assertThrows(IllegalArgumentException.class,
                () -> new Reducao("EAE-1", " ", pMaior, pMenor, conc));
        assertThrows(IllegalArgumentException.class,
                () -> new Reducao("EAE-1", "Celarent", null, pMenor, conc));
        assertThrows(IllegalArgumentException.class,
                () -> new Reducao("EAE-1", "Celarent", pMaior, null, conc));
        assertThrows(IllegalArgumentException.class,
                () -> new Reducao("EAE-1", "Celarent", pMaior, pMenor, null));
    }
}
