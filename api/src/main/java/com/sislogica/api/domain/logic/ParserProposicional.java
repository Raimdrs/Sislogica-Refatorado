package com.sislogica.api.domain.logic;

import java.util.ArrayList;
import java.util.List;

/**
 * Analisador léxico e sintático (recursive-descent) para fórmulas proposicionais clássicas.
 *
 * Operadores suportados (da maior para a menor precedência):
 *  - Parênteses: ( )
 *  - Negação: ~, !, ¬
 *  - Conjunção (E): ^, &, &&, ∧
 *  - Disjunção (OU): v, |, ||, ∨
 *  - Implicação: ->, → (associativa à direita)
 *  - Bicondicional: <->, ↔
 *
 * Variáveis proposicionais: letra maiúscula opcionalmente seguida de dígitos (ex.: P, Q, R, P1).
 */
public class ParserProposicional {

    private enum TipoToken {
        VAR, NOT, AND, OR, IMPLIES, IFF, LPAREN, RPAREN, EOF
    }

    private record Token(TipoToken tipo, String lexema, int posicao) {
    }

    public FormulaProposicional parse(String entrada) {
        if (entrada == null || entrada.isBlank()) {
            throw new IllegalArgumentException("A fórmula proposicional não pode ser vazia.");
        }

        List<Token> tokens = tokenizar(entrada);
        ParserState state = new ParserState(tokens);
        FormulaProposicional raiz = state.parseBicondicional();

        if (state.atual().tipo() != TipoToken.EOF) {
            throw new IllegalArgumentException(
                    "Token inesperado '" + state.atual().lexema() + "' na posição " + state.atual().posicao() + "."
            );
        }

        return raiz;
    }

    private List<Token> tokenizar(String entrada) {
        List<Token> tokens = new ArrayList<>();
        int i = 0;
        int len = entrada.length();

        while (i < len) {
            char c = entrada.charAt(i);

            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            if (c == '<' && i + 2 < len && entrada.charAt(i + 1) == '-' && entrada.charAt(i + 2) == '>') {
                tokens.add(new Token(TipoToken.IFF, "<->", i));
                i += 3;
            } else if (c == '-' && i + 1 < len && entrada.charAt(i + 1) == '>') {
                tokens.add(new Token(TipoToken.IMPLIES, "->", i));
                i += 2;
            } else if (c == '&' && i + 1 < len && entrada.charAt(i + 1) == '&') {
                tokens.add(new Token(TipoToken.AND, "&&", i));
                i += 2;
            } else if (c == '|' && i + 1 < len && entrada.charAt(i + 1) == '|') {
                tokens.add(new Token(TipoToken.OR, "||", i));
                i += 2;
            } else if (c == '~' || c == '!' || c == '¬') {
                tokens.add(new Token(TipoToken.NOT, String.valueOf(c), i));
                i++;
            } else if (c == '^' || c == '&' || c == '∧') {
                tokens.add(new Token(TipoToken.AND, String.valueOf(c), i));
                i++;
            } else if (c == 'v' || c == '|' || c == '∨') {
                tokens.add(new Token(TipoToken.OR, String.valueOf(c), i));
                i++;
            } else if (c == '→') {
                tokens.add(new Token(TipoToken.IMPLIES, "→", i));
                i++;
            } else if (c == '↔') {
                tokens.add(new Token(TipoToken.IFF, "↔", i));
                i++;
            } else if (c == '(') {
                tokens.add(new Token(TipoToken.LPAREN, "(", i));
                i++;
            } else if (c == ')') {
                tokens.add(new Token(TipoToken.RPAREN, ")", i));
                i++;
            } else if (c >= 'A' && c <= 'Z') {
                int inicio = i;
                i++;
                while (i < len && Character.isDigit(entrada.charAt(i))) {
                    i++;
                }
                tokens.add(new Token(TipoToken.VAR, entrada.substring(inicio, i), inicio));
            } else {
                throw new IllegalArgumentException(
                        "Caractere ou operador inválido na posição " + i + ": '" + c + "'."
                );
            }
        }

        tokens.add(new Token(TipoToken.EOF, "", len));
        return tokens;
    }

    private static class ParserState {
        private final List<Token> tokens;
        private int cursor = 0;

        ParserState(List<Token> tokens) {
            this.tokens = tokens;
        }

        Token atual() {
            return tokens.get(cursor);
        }

        Token avancar() {
            Token t = tokens.get(cursor);
            if (cursor < tokens.size() - 1) {
                cursor++;
            }
            return t;
        }

        FormulaProposicional parseBicondicional() {
            FormulaProposicional esquerda = parseImplicacao();
            while (atual().tipo() == TipoToken.IFF) {
                avancar();
                FormulaProposicional direita = parseImplicacao();
                esquerda = new FormulaProposicional.Binaria(
                        FormulaProposicional.OperadorBinario.BICONDICIONAL, esquerda, direita
                );
            }
            return esquerda;
        }

        FormulaProposicional parseImplicacao() {
            FormulaProposicional esquerda = parseDisjuncao();
            if (atual().tipo() == TipoToken.IMPLIES) {
                avancar();
                FormulaProposicional direita = parseImplicacao();
                return new FormulaProposicional.Binaria(
                        FormulaProposicional.OperadorBinario.IMPLICACAO, esquerda, direita
                );
            }
            return esquerda;
        }

        FormulaProposicional parseDisjuncao() {
            FormulaProposicional esquerda = parseConjuncao();
            while (atual().tipo() == TipoToken.OR) {
                avancar();
                FormulaProposicional direita = parseConjuncao();
                esquerda = new FormulaProposicional.Binaria(
                        FormulaProposicional.OperadorBinario.DISJUNCAO, esquerda, direita
                );
            }
            return esquerda;
        }

        FormulaProposicional parseConjuncao() {
            FormulaProposicional esquerda = parseUnario();
            while (atual().tipo() == TipoToken.AND) {
                avancar();
                FormulaProposicional direita = parseUnario();
                esquerda = new FormulaProposicional.Binaria(
                        FormulaProposicional.OperadorBinario.CONJUNCAO, esquerda, direita
                );
            }
            return esquerda;
        }

        FormulaProposicional parseUnario() {
            if (atual().tipo() == TipoToken.NOT) {
                avancar();
                return new FormulaProposicional.Negacao(parseUnario());
            }
            return parsePrimario();
        }

        FormulaProposicional parsePrimario() {
            Token token = atual();
            if (token.tipo() == TipoToken.VAR) {
                avancar();
                return new FormulaProposicional.Variavel(token.lexema());
            }
            if (token.tipo() == TipoToken.LPAREN) {
                avancar();
                FormulaProposicional interna = parseBicondicional();
                if (atual().tipo() != TipoToken.RPAREN) {
                    throw new IllegalArgumentException(
                            "Parêntese de fechamento ')' esperado na posição " + atual().posicao() + "."
                    );
                }
                avancar();
                return interna;
            }
            throw new IllegalArgumentException(
                    "Esperada variável proposicional ou '(' na posição " + token.posicao() + "."
            );
        }
    }
}
