package domain

import (
	"context"
	"encoding/json"
	"fmt"
	"sort"
	"strings"
	"unicode"
)

// RowObserver é uma interface pequena opcional para observar cada linha avaliada
// (útil para telemetria ou testes determinísticos de cancelamento/prazo sem acoplamento).
type RowObserver interface {
	OnRowEvaluated(ctx context.Context, rowIndex int) error
}

// LogicEngine executa parsing formal (AST) e validação semântica de equivalências proposicionais.
type LogicEngine struct {
	observer RowObserver
}

// NewLogicEngine cria uma nova instância pura do motor de lógica proposicional.
func NewLogicEngine(observer RowObserver) *LogicEngine {
	return &LogicEngine{observer: observer}
}

type tokenType int

const (
	tokVar tokenType = iota
	tokNot
	tokAnd
	tokOr
	tokImplies
	tokIff
	tokLParen
	tokRParen
	tokEOF
)

type token struct {
	kind   tokenType
	lexeme string
	pos    int
}

// Parse realiza análise léxica e sintática (recursive-descent) de uma fórmula proposicional,
// respeitando o cancelamento/prazo do contexto recebido.
func (e *LogicEngine) Parse(ctx context.Context, expr string) (*ParseResult, error) {
	select {
	case <-ctx.Done():
		return nil, fmt.Errorf("parsing cancelado antes do início: %w", ctx.Err())
	default:
	}

	trimmed := strings.TrimSpace(expr)
	if trimmed == "" {
		return nil, fmt.Errorf("expressão obrigatória: %w", ErrEmptyExpression)
	}

	tokens, err := tokenize(ctx, trimmed)
	if err != nil {
		return nil, err
	}

	p := &parserState{tokens: tokens}
	ast, err := p.parseIff(ctx)
	if err != nil {
		return nil, err
	}

	if p.current().kind != tokEOF {
		if p.current().kind == tokRParen {
			return nil, fmt.Errorf("parêntese ')' extra na posição %d: %w", p.current().pos, ErrUnbalancedParentheses)
		}
		return nil, fmt.Errorf("token inesperado %q na posição %d: %w", p.current().lexeme, p.current().pos, ErrInvalidSyntax)
	}

	vars := ast.CollectVariables()
	rawJSON, err := json.Marshal(ast)
	if err != nil {
		return nil, fmt.Errorf("falha ao serializar AST: %w", err)
	}

	return &ParseResult{
		Expression: trimmed,
		Variables:  vars,
		AST:        ast,
		ASTJSON:    string(rawJSON),
	}, nil
}

// ValidateEquivalence unifica as variáveis de formulaA e formulaB e compara suas tabelas-verdade
// respeitando o prazo do contexto em cada iteração.
func (e *LogicEngine) ValidateEquivalence(ctx context.Context, formulaA, formulaB string) (*EquivalenceResult, error) {
	parsedA, err := e.Parse(ctx, formulaA)
	if err != nil {
		return nil, fmt.Errorf("fórmula A inválida: %w", err)
	}

	parsedB, err := e.Parse(ctx, formulaB)
	if err != nil {
		return nil, fmt.Errorf("fórmula B inválida: %w", err)
	}

	varSet := make(map[string]struct{})
	for _, v := range parsedA.Variables {
		varSet[v] = struct{}{}
	}
	for _, v := range parsedB.Variables {
		varSet[v] = struct{}{}
	}

	if len(varSet) > MaxVariables {
		return nil, fmt.Errorf("foram encontradas %d variáveis (máximo %d): %w", len(varSet), MaxVariables, ErrTooManyVariables)
	}

	variables := make([]string, 0, len(varSet))
	for v := range varSet {
		variables = append(variables, v)
	}
	sort.Strings(variables)

	n := len(variables)
	totalRows := 1 << n
	table := make([]TruthTableRow, 0, totalRows)
	var counterexample map[string]bool

	for row := 0; row < totalRows; row++ {
		select {
		case <-ctx.Done():
			return nil, fmt.Errorf("avaliação interrompida na linha %d de %d: %w", row, totalRows, ctx.Err())
		default:
		}

		valuation := make(map[string]bool, n)
		for bit := 0; bit < n; bit++ {
			valuation[variables[bit]] = ((row >> (n - 1 - bit)) & 1) == 0
		}

		resA, err := parsedA.AST.Evaluate(valuation)
		if err != nil {
			return nil, fmt.Errorf("falha ao avaliar fórmula A: %w", err)
		}
		resB, err := parsedB.AST.Evaluate(valuation)
		if err != nil {
			return nil, fmt.Errorf("falha ao avaliar fórmula B: %w", err)
		}

		eq := resA == resB
		if !eq && counterexample == nil {
			counterexample = make(map[string]bool, n)
			for k, v := range valuation {
				counterexample[k] = v
			}
		}

		table = append(table, TruthTableRow{
			Valuation:  valuation,
			ResultA:    resA,
			ResultB:    resB,
			Equivalent: eq,
		})

		if e.observer != nil {
			if err := e.observer.OnRowEvaluated(ctx, row); err != nil {
				return nil, fmt.Errorf("observador interrompeu avaliação na linha %d: %w", row, err)
			}
		}
	}

	return &EquivalenceResult{
		FormulaA:       parsedA.Expression,
		FormulaB:       parsedB.Expression,
		Equivalent:     counterexample == nil,
		Variables:      variables,
		Counterexample: counterexample,
		TruthTable:     table,
	}, nil
}

func tokenize(ctx context.Context, input string) ([]token, error) {
	runes := []rune(input)
	n := len(runes)
	tokens := make([]token, 0, n)

	for i := 0; i < n; {
		select {
		case <-ctx.Done():
			return nil, fmt.Errorf("tokenização cancelada na posição %d: %w", i, ctx.Err())
		default:
		}

		ch := runes[i]
		if unicode.IsSpace(ch) {
			i++
			continue
		}

		if ch == '<' && i+2 < n && runes[i+1] == '-' && runes[i+2] == '>' {
			tokens = append(tokens, token{kind: tokIff, lexeme: "<->", pos: i})
			i += 3
		} else if ch == '-' && i+1 < n && runes[i+1] == '>' {
			tokens = append(tokens, token{kind: tokImplies, lexeme: "->", pos: i})
			i += 2
		} else if ch == '&' && i+1 < n && runes[i+1] == '&' {
			tokens = append(tokens, token{kind: tokAnd, lexeme: "&&", pos: i})
			i += 2
		} else if ch == '|' && i+1 < n && runes[i+1] == '|' {
			tokens = append(tokens, token{kind: tokOr, lexeme: "||", pos: i})
			i += 2
		} else if ch == '~' || ch == '!' || ch == '¬' {
			tokens = append(tokens, token{kind: tokNot, lexeme: string(ch), pos: i})
			i++
		} else if ch == '^' || ch == '&' || ch == '∧' {
			tokens = append(tokens, token{kind: tokAnd, lexeme: string(ch), pos: i})
			i++
		} else if ch == 'v' || ch == '|' || ch == '∨' {
			tokens = append(tokens, token{kind: tokOr, lexeme: string(ch), pos: i})
			i++
		} else if ch == '→' {
			tokens = append(tokens, token{kind: tokImplies, lexeme: "→", pos: i})
			i++
		} else if ch == '↔' {
			tokens = append(tokens, token{kind: tokIff, lexeme: "↔", pos: i})
			i++
		} else if ch == '(' {
			tokens = append(tokens, token{kind: tokLParen, lexeme: "(", pos: i})
			i++
		} else if ch == ')' {
			tokens = append(tokens, token{kind: tokRParen, lexeme: ")", pos: i})
			i++
		} else if ch >= 'A' && ch <= 'Z' {
			start := i
			i++
			for i < n && runes[i] >= '0' && runes[i] <= '9' {
				i++
			}
			tokens = append(tokens, token{kind: tokVar, lexeme: string(runes[start:i]), pos: start})
		} else {
			return nil, fmt.Errorf("caractere inválido %q na posição %d: %w", string(ch), i, ErrInvalidSyntax)
		}
	}

	tokens = append(tokens, token{kind: tokEOF, lexeme: "", pos: n})
	return tokens, nil
}

type parserState struct {
	tokens []token
	cursor int
}

func (p *parserState) current() token {
	return p.tokens[p.cursor]
}

func (p *parserState) advance() token {
	t := p.tokens[p.cursor]
	if p.cursor < len(p.tokens)-1 {
		p.cursor++
	}
	return t
}

func (p *parserState) parseIff(ctx context.Context) (*ASTNode, error) {
	left, err := p.parseImplies(ctx)
	if err != nil {
		return nil, err
	}
	for p.current().kind == tokIff {
		p.advance()
		right, err := p.parseImplies(ctx)
		if err != nil {
			return nil, err
		}
		left = &ASTNode{Kind: NodeBinary, Operator: OpIff, Left: left, Right: right}
	}
	return left, nil
}

func (p *parserState) parseImplies(ctx context.Context) (*ASTNode, error) {
	left, err := p.parseOr(ctx)
	if err != nil {
		return nil, err
	}
	if p.current().kind == tokImplies {
		p.advance()
		right, err := p.parseImplies(ctx)
		if err != nil {
			return nil, err
		}
		return &ASTNode{Kind: NodeBinary, Operator: OpImplies, Left: left, Right: right}, nil
	}
	return left, nil
}

func (p *parserState) parseOr(ctx context.Context) (*ASTNode, error) {
	left, err := p.parseAnd(ctx)
	if err != nil {
		return nil, err
	}
	for p.current().kind == tokOr {
		p.advance()
		right, err := p.parseAnd(ctx)
		if err != nil {
			return nil, err
		}
		left = &ASTNode{Kind: NodeBinary, Operator: OpOr, Left: left, Right: right}
	}
	return left, nil
}

func (p *parserState) parseAnd(ctx context.Context) (*ASTNode, error) {
	left, err := p.parseUnary(ctx)
	if err != nil {
		return nil, err
	}
	for p.current().kind == tokAnd {
		p.advance()
		right, err := p.parseUnary(ctx)
		if err != nil {
			return nil, err
		}
		left = &ASTNode{Kind: NodeBinary, Operator: OpAnd, Left: left, Right: right}
	}
	return left, nil
}

func (p *parserState) parseUnary(ctx context.Context) (*ASTNode, error) {
	select {
	case <-ctx.Done():
		return nil, fmt.Errorf("parsing cancelado: %w", ctx.Err())
	default:
	}

	if p.current().kind == tokNot {
		p.advance()
		operand, err := p.parseUnary(ctx)
		if err != nil {
			return nil, err
		}
		return &ASTNode{Kind: NodeNegation, Right: operand}, nil
	}
	return p.parsePrimary(ctx)
}

func (p *parserState) parsePrimary(ctx context.Context) (*ASTNode, error) {
	tok := p.current()
	switch tok.kind {
	case tokVar:
		p.advance()
		return &ASTNode{Kind: NodeVariable, Value: tok.lexeme}, nil

	case tokLParen:
		p.advance()
		inner, err := p.parseIff(ctx)
		if err != nil {
			return nil, err
		}
		if p.current().kind != tokRParen {
			return nil, fmt.Errorf("esperado ')' na posição %d: %w", p.current().pos, ErrUnbalancedParentheses)
		}
		p.advance()
		return inner, nil

	default:
		return nil, fmt.Errorf("esperada variável ou '(' na posição %d: %w", tok.pos, ErrInvalidSyntax)
	}
}
