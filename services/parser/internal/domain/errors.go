package domain

import "errors"

// Erros sentinela do domínio de análise proposicional.
// Podem ser verificados com errors.Is e encapsulados com fmt.Errorf("...: %w", err).
var (
	// ErrEmptyExpression indica que a expressão recebida está vazia ou contém apenas espaços.
	ErrEmptyExpression = errors.New("expressão proposicional vazia")

	// ErrUnbalancedParentheses indica parênteses abertos sem fechamento correspondente ou vice-versa.
	ErrUnbalancedParentheses = errors.New("parênteses desbalanceados")

	// ErrInvalidSyntax indica operador, caractere ou sequência de tokens inválida na fórmula.
	ErrInvalidSyntax = errors.New("sintaxe proposicional inválida")

	// ErrTooManyVariables indica que a fórmula ultrapassa o limite seguro de variáveis distintas.
	ErrTooManyVariables = errors.New("limite máximo de variáveis proposicionais excedido")
)
