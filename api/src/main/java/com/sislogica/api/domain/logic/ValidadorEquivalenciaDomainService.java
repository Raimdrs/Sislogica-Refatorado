package com.sislogica.api.domain.logic;

import com.sislogica.api.domain.model.LinhaTabelaVerdade;
import com.sislogica.api.domain.model.ResultadoEquivalencia;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Serviço de domínio puro responsável por unificar variáveis proposicionais,
 * construir e comparar as tabelas-verdade de duas fórmulas (A e B) e identificar
 * o primeiro contraexemplo em caso de não equivalência.
 */
public class ValidadorEquivalenciaDomainService {

    public static final int MAX_VARIAVEIS = 12;

    private final ParserProposicional parser;

    public ValidadorEquivalenciaDomainService() {
        this(new ParserProposicional());
    }

    public ValidadorEquivalenciaDomainService(ParserProposicional parser) {
        this.parser = parser;
    }

    public ResultadoEquivalencia validar(String formulaA, String formulaB) {
        if (formulaA == null || formulaA.isBlank()) {
            throw new IllegalArgumentException("A fórmula A é obrigatória.");
        }
        if (formulaB == null || formulaB.isBlank()) {
            throw new IllegalArgumentException("A fórmula B é obrigatória.");
        }

        String aLimpa = formulaA.trim();
        String bLimpa = formulaB.trim();

        FormulaProposicional astA = parser.parse(aLimpa);
        FormulaProposicional astB = parser.parse(bLimpa);

        Set<String> conjuntoVariaveis = new TreeSet<>();
        astA.coletarVariaveis(conjuntoVariaveis);
        astB.coletarVariaveis(conjuntoVariaveis);

        if (conjuntoVariaveis.size() > MAX_VARIAVEIS) {
            throw new IllegalArgumentException(
                    "A comparação suporta no máximo " + MAX_VARIAVEIS + " variáveis proposicionais distintas."
            );
        }

        List<String> variaveis = List.copyOf(conjuntoVariaveis);
        int n = variaveis.size();
        int totalLinhas = 1 << n;

        List<LinhaTabelaVerdade> tabela = new ArrayList<>(totalLinhas);
        Map<String, Boolean> primeiroContraexemplo = null;

        for (int linha = 0; linha < totalLinhas; linha++) {
            Map<String, Boolean> valoracao = new LinkedHashMap<>();
            for (int bit = 0; bit < n; bit++) {
                boolean valor = ((linha >> (n - 1 - bit)) & 1) == 0;
                valoracao.put(variaveis.get(bit), valor);
            }

            boolean resA = astA.avaliar(valoracao);
            boolean resB = astB.avaliar(valoracao);
            boolean linhaEquivalente = (resA == resB);

            if (!linhaEquivalente && primeiroContraexemplo == null) {
                primeiroContraexemplo = new LinkedHashMap<>(valoracao);
            }

            tabela.add(new LinhaTabelaVerdade(valoracao, resA, resB, linhaEquivalente));
        }

        boolean equivalentes = (primeiroContraexemplo == null);

        return new ResultadoEquivalencia(
                aLimpa,
                bLimpa,
                equivalentes,
                variaveis,
                primeiroContraexemplo,
                tabela
        );
    }
}
