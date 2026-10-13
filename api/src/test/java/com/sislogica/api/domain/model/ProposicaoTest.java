package com.sislogica.api.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProposicaoTest {

    @Test
    @DisplayName("Deve instanciar uma Proposicao com quantificador, sujeito, predicado e transliteração opcional")
    void deveInstanciarProposicaoComTransliteracao() {
        Proposicao proposicao = new Proposicao(
                1L,
                Quantificador.A,
                Termo.M,
                Termo.T,
                "Todo homem é mortal"
        );

        assertEquals(1L, proposicao.getId());
        assertEquals(Quantificador.A, proposicao.getQuantificador());
        assertEquals("Universal Afirmativa", proposicao.getQuantificador().getDescricao());
        assertEquals(Termo.M, proposicao.getSujeito());
        assertEquals("Termo Médio", proposicao.getSujeito().getDescricao());
        assertEquals(Termo.T, proposicao.getPredicado());
        assertEquals("Termo Maior", proposicao.getPredicado().getDescricao());
        assertEquals("Todo homem é mortal", proposicao.getTransliteracao());
    }

    @Test
    @DisplayName("Deve instanciar uma Proposicao sem transliteração (opcional)")
    void deveInstanciarProposicaoSemTransliteracao() {
        Proposicao proposicao = new Proposicao(Quantificador.E, Termo.t, Termo.M);

        assertNull(proposicao.getId());
        assertEquals(Quantificador.E, proposicao.getQuantificador());
        assertEquals(Termo.t, proposicao.getSujeito());
        assertEquals("Termo Menor", proposicao.getSujeito().getDescricao());
        assertEquals(Termo.M, proposicao.getPredicado());
        assertNull(proposicao.getTransliteracao());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando quantificador, sujeito ou predicado forem nulos")
    void deveValidarCamposObrigatorios() {
        assertThrows(IllegalArgumentException.class,
                () -> new Proposicao(null, Termo.M, Termo.T));
        assertThrows(IllegalArgumentException.class,
                () -> new Proposicao(Quantificador.I, null, Termo.T));
        assertThrows(IllegalArgumentException.class,
                () -> new Proposicao(Quantificador.O, Termo.t, null));
    }
}
