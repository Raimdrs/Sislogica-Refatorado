package domain

import (
	"fmt"
	"sort"
)

// MaxVariables limita a explosão combinatória 2^n durante a avaliação de tabelas-verdade.
const MaxVariables = 12

// NodeKind identifica o tipo de nó da Árvore Sintática Abstrata (AST).
type NodeKind string

const (
	NodeVariable NodeKind = "VARIABLE"
	NodeNegation NodeKind = "NEGATION"
	NodeBinary   NodeKind = "BINARY"
)

// BinaryOp identifica os conectivos binários da lógica proposicional clássica.
type BinaryOp string

const (
	OpAnd     BinaryOp = "AND"
	OpOr      BinaryOp = "OR"
	OpImplies BinaryOp = "IMPLIES"
	OpIff     BinaryOp = "IFF"
)

// ASTNode representa um nó da Árvore Sintática Abstrata de uma fórmula proposicional.
type ASTNode struct {
	Kind     NodeKind `json:"kind"`
	Value    string   `json:"value,omitempty"`
	Operator BinaryOp `json:"operator,omitempty"`
	Left     *ASTNode `json:"left,omitempty"`
	Right    *ASTNode `json:"right,omitempty"`
}

// Evaluate avalia recursivamente a AST sob a valoração informada.
func (n *ASTNode) Evaluate(valuation map[string]bool) (bool, error) {
	if n == nil {
		return false, fmt.Errorf("nó AST nulo: %w", ErrInvalidSyntax)
	}

	switch n.Kind {
	case NodeVariable:
		val, ok := valuation[n.Value]
		if !ok {
			return false, fmt.Errorf("variável %q sem valoração: %w", n.Value, ErrInvalidSyntax)
		}
		return val, nil

	case NodeNegation:
		res, err := n.Right.Evaluate(valuation)
		if err != nil {
			return false, err
		}
		return !res, nil

	case NodeBinary:
		left, err := n.Left.Evaluate(valuation)
		if err != nil {
			return false, err
		}
		right, err := n.Right.Evaluate(valuation)
		if err != nil {
			return false, err
		}
		switch n.Operator {
		case OpAnd:
			return left && right, nil
		case OpOr:
			return left || right, nil
		case OpImplies:
			return !left || right, nil
		case OpIff:
			return left == right, nil
		default:
			return false, fmt.Errorf("operador binário desconhecido %q: %w", n.Operator, ErrInvalidSyntax)
		}

	default:
		return false, fmt.Errorf("tipo de nó desconhecido %q: %w", n.Kind, ErrInvalidSyntax)
	}
}

// CollectVariables coleta as variáveis distintas presentes na AST em ordem alfabética.
func (n *ASTNode) CollectVariables() []string {
	seen := make(map[string]struct{})
	n.collect(seen)

	vars := make([]string, 0, len(seen))
	for v := range seen {
		vars = append(vars, v)
	}
	sort.Strings(vars)
	return vars
}

func (n *ASTNode) collect(seen map[string]struct{}) {
	if n == nil {
		return
	}
	if n.Kind == NodeVariable {
		seen[n.Value] = struct{}{}
		return
	}
	n.Left.collect(seen)
	n.Right.collect(seen)
}

// ParseResult representa o resultado de domínio do parsing formal de uma fórmula.
type ParseResult struct {
	Expression string
	Variables  []string
	AST        *ASTNode
	ASTJSON    string
}

// TruthTableRow representa uma linha comparativa de tabela-verdade para A e B.
type TruthTableRow struct {
	Valuation  map[string]bool
	ResultA    bool
	ResultB    bool
	Equivalent bool
}

// EquivalenceResult representa o resultado de domínio da validação semântica entre A e B.
type EquivalenceResult struct {
	FormulaA       string
	FormulaB       string
	Equivalent     bool
	Variables      []string
	Counterexample map[string]bool
	TruthTable     []TruthTableRow
}
