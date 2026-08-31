package main

import (
	"fmt"
	"strings"
	"unicode"
)

// PropositionResult representa o resultado do parsing de uma fórmula lógica
type PropositionResult struct {
	Expression string   `json:"expression"`
	Variables  []string `json:"variables"`
	Valid      bool     `json:"valid"`
}

// ExtractVariables extrai variáveis proposicionais (ex: P, Q, R) de uma fórmula
func ExtractVariables(expr string) []string {
	seen := make(map[string]bool)
	var vars []string

	for _, ch := range expr {
		if unicode.IsUpper(ch) && unicode.IsLetter(ch) {
			letter := string(ch)
			if !seen[letter] {
				seen[letter] = true
				vars = append(vars, letter)
			}
		}
	}
	return vars
}

// ValidateParentheses checa se os parênteses da fórmula estão balanceados
func ValidateParentheses(expr string) bool {
	balance := 0
	for _, r := range expr {
		if r == '(' {
			balance++
		} else if r == ')' {
			balance--
			if balance < 0 {
				return false
			}
		}
	}
	return balance == 0
}

// ParseProposition analisa sintaticamente a proposição lógica básica
func ParseProposition(expr string) (*PropositionResult, error) {
	trimmed := strings.TrimSpace(expr)
	if trimmed == "" {
		return nil, fmt.Errorf("a expressão proposicional não pode ser vazia")
	}

	if !ValidateParentheses(trimmed) {
		return nil, fmt.Errorf("parênteses desbalanceados na expressão")
	}

	variables := ExtractVariables(trimmed)
	return &PropositionResult{
		Expression: trimmed,
		Variables:  variables,
		Valid:      true,
	}, nil
}

func main() {
	fmt.Println("🚀 Sílógica Logic Parser Service (Go) iniciado na porta 50051...")
	sample := "(P & Q) -> R"
	res, err := ParseProposition(sample)
	if err != nil {
		fmt.Printf("Erro: %v\n", err)
		return
	}
	fmt.Printf("Fórmula: %s | Variáveis detectadas: %v | Válida: %t\n", res.Expression, res.Variables, res.Valid)
}
