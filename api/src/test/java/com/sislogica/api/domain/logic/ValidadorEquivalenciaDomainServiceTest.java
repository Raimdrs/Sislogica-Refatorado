package com.sislogica.api.domain.logic;

import com.sislogica.api.domain.model.ResultadoEquivalencia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ValidadorEquivalenciaDomainServiceTest {

    private ValidadorEquivalenciaDomainService validador;

    @BeforeEach
    void setUp() {
        validador = new ValidadorEquivalenciaDomainService();
    }

    @Test
    @DisplayName("Deve validar Lei de De Morgan (conjunção): ~(P ^ Q) ≡ ~P v ~Q")
    void deveValidarLeiDeMorganConjuncao() {
        ResultadoEquivalencia res = validador.validar("~(P ^ Q)", "~P v ~Q");

        assertTrue(res.equivalentes());
        assertNull(res.contraexemplo());
        assertEquals(List.of("P", "Q"), res.variaveis());
        assertEquals(4, res.tabelaVerdade().size());
        assertTrue(res.tabelaVerdade().stream().allMatch(l -> l.equivalente()));
    }

    @Test
    @DisplayName("Deve validar Lei de De Morgan (disjunção) com operadores Unicode: ¬(P ∨ Q) ≡ ¬P ∧ ¬Q")
    void deveValidarLeiDeMorganDisjuncaoUnicode() {
        ResultadoEquivalencia res = validador.validar("¬(P ∨ Q)", "¬P ∧ ¬Q");

        assertTrue(res.equivalentes());
        assertNull(res.contraexemplo());
        assertEquals(List.of("P", "Q"), res.variaveis());
        assertEquals(4, res.tabelaVerdade().size());
    }

    @Test
    @DisplayName("Deve validar Implicação Material: P -> Q ≡ ~P v Q")
    void deveValidarImplicacaoMaterial() {
        ResultadoEquivalencia res = validador.validar("P -> Q", "~P v Q");

        assertTrue(res.equivalentes());
        assertNull(res.contraexemplo());
        assertEquals(List.of("P", "Q"), res.variaveis());
        assertEquals(4, res.tabelaVerdade().size());
    }

    @Test
    @DisplayName("Deve validar Dupla Negação: ~~P ≡ P")
    void deveValidarDuplaNegacao() {
        ResultadoEquivalencia res = validador.validar("~~P", "P");

        assertTrue(res.equivalentes());
        assertNull(res.contraexemplo());
        assertEquals(List.of("P"), res.variaveis());
        assertEquals(2, res.tabelaVerdade().size());
    }

    @Test
    @DisplayName("Deve validar Contrapositiva: P -> Q ≡ ~Q -> ~P")
    void deveValidarContrapositiva() {
        ResultadoEquivalencia res = validador.validar("P -> Q", "~Q -> ~P");

        assertTrue(res.equivalentes());
        assertNull(res.contraexemplo());
    }

    @Test
    @DisplayName("Deve validar Bicondicional: P <-> Q ≡ (P -> Q) ^ (Q -> P)")
    void deveValidarBicondicional() {
        ResultadoEquivalencia res = validador.validar("P <-> Q", "(P -> Q) ^ (Q -> P)");

        assertTrue(res.equivalentes());
        assertNull(res.contraexemplo());
        assertEquals(4, res.tabelaVerdade().size());
    }

    @Test
    @DisplayName("Deve validar Lei Distributiva com 3 variáveis: P ^ (Q v R) ≡ (P ^ Q) v (P ^ R)")
    void deveValidarLeiDistributivaTresVariaveis() {
        ResultadoEquivalencia res = validador.validar("P ^ (Q v R)", "(P ^ Q) v (P ^ R)");

        assertTrue(res.equivalentes());
        assertNull(res.contraexemplo());
        assertEquals(List.of("P", "Q", "R"), res.variaveis());
        assertEquals(8, res.tabelaVerdade().size());
    }

    @Test
    @DisplayName("Deve unificar variáveis distintas entre formulaA e formulaB (Lei da Absorção): P ≡ P v (P ^ Q)")
    void deveUnificarVariaveisEntreFormulas() {
        ResultadoEquivalencia res = validador.validar("P", "P v (P ^ Q)");

        assertTrue(res.equivalentes());
        assertEquals(List.of("P", "Q"), res.variaveis());
        assertEquals(4, res.tabelaVerdade().size());
    }

    @Test
    @DisplayName("Deve retornar equivalentes=false e primeiro contraexemplo {P: true, Q: false} para P -> Q vs Q -> P")
    void deveRetornarContraexemploParaImplicacaoNaoEquivalente() {
        ResultadoEquivalencia res = validador.validar("P -> Q", "Q -> P");

        assertFalse(res.equivalentes());
        assertNotNull(res.contraexemplo());
        assertEquals(Map.of("P", true, "Q", false), res.contraexemplo());
        assertEquals(4, res.tabelaVerdade().size());
    }

    @Test
    @DisplayName("Deve retornar equivalentes=false e contraexemplo para P ^ Q vs P v Q")
    void deveRetornarContraexemploParaConjuncaoVsDisjuncao() {
        ResultadoEquivalencia res = validador.validar("P ^ Q", "P v Q");

        assertFalse(res.equivalentes());
        assertEquals(Map.of("P", true, "Q", false), res.contraexemplo());
    }

    @Test
    @DisplayName("Deve rejeitar fórmulas sintaticamente inválidas com IllegalArgumentException")
    void deveRejeitarFormulasInvalidas() {
        assertThrows(IllegalArgumentException.class, () -> validador.validar("", "P"));
        assertThrows(IllegalArgumentException.class, () -> validador.validar("(P -> Q", "P -> Q"));
        assertThrows(IllegalArgumentException.class, () -> validador.validar("P ^^ Q", "P ^ Q"));
        assertThrows(IllegalArgumentException.class, () -> validador.validar("P + Q", "P v Q"));
        assertThrows(IllegalArgumentException.class, () -> validador.validar("P Q", "P ^ Q"));
        assertThrows(IllegalArgumentException.class, () -> validador.validar("P ->", "P"));
    }

    @Test
    @DisplayName("Deve rejeitar comparação que ultrapasse o limite de variáveis distintas")
    void deveRejeitarExcessoDeVariaveis() {
        String formulaGrande = "A ^ B ^ C ^ D ^ E ^ F ^ G ^ H ^ I ^ J ^ K ^ L ^ M";
        assertThrows(IllegalArgumentException.class, () -> validador.validar(formulaGrande, "A"));
    }
}
