package domain_test

import (
	"context"
	"errors"
	"reflect"
	"testing"
	"time"

	"github.com/sislogica/services/parser/internal/domain"
)

type slowObserver struct {
	delay time.Duration
}

func (s slowObserver) OnRowEvaluated(ctx context.Context, _ int) error {
	select {
	case <-ctx.Done():
		return ctx.Err()
	case <-time.After(s.delay):
		return nil
	}
}

func TestParseValidExpressions(t *testing.T) {
	engine := domain.NewLogicEngine(nil)
	ctx := context.Background()

	tests := []struct {
		expr     string
		expected []string
	}{
		{"(P & Q) -> R", []string{"P", "Q", "R"}},
		{"~(P ^ Q)", []string{"P", "Q"}},
		{"¬(P ∨ Q) ↔ (¬P ∧ ¬Q)", []string{"P", "Q"}},
		{"~~P", []string{"P"}},
	}

	for _, tc := range tests {
		res, err := engine.Parse(ctx, tc.expr)
		if err != nil {
			t.Fatalf("Parse(%q) retornou erro inesperado: %v", tc.expr, err)
		}
		if !reflect.DeepEqual(res.Variables, tc.expected) {
			t.Errorf("Parse(%q).Variables = %v; esperado %v", tc.expr, res.Variables, tc.expected)
		}
		if res.AST == nil || res.ASTJSON == "" {
			t.Errorf("Parse(%q) deveria gerar AST e ASTJSON preenchidos", tc.expr)
		}
	}
}

func TestParseInvalidExpressionsAndNamedErrors(t *testing.T) {
	engine := domain.NewLogicEngine(nil)
	ctx := context.Background()

	tests := []struct {
		expr        string
		expectedErr error
	}{
		{"", domain.ErrEmptyExpression},
		{"   ", domain.ErrEmptyExpression},
		{"(P -> Q", domain.ErrUnbalancedParentheses},
		{"P -> Q)", domain.ErrUnbalancedParentheses},
		{"P ^^ Q", domain.ErrInvalidSyntax},
		{"P + Q", domain.ErrInvalidSyntax},
		{"P Q", domain.ErrInvalidSyntax},
		{"P ->", domain.ErrInvalidSyntax},
	}

	for _, tc := range tests {
		_, err := engine.Parse(ctx, tc.expr)
		if !errors.Is(err, tc.expectedErr) {
			t.Errorf("Parse(%q) erro = %v; esperado errors.Is(_, %v)", tc.expr, err, tc.expectedErr)
		}
	}
}

func TestValidateEquivalenceClassicalLaws(t *testing.T) {
	engine := domain.NewLogicEngine(nil)
	ctx := context.Background()

	tests := []struct {
		name     string
		formulaA string
		formulaB string
		vars     []string
		rows     int
	}{
		{"De Morgan conjuncao", "~(P ^ Q)", "~P v ~Q", []string{"P", "Q"}, 4},
		{"De Morgan disjuncao unicode", "¬(P ∨ Q)", "¬P ∧ ¬Q", []string{"P", "Q"}, 4},
		{"Implicacao material", "P -> Q", "~P v Q", []string{"P", "Q"}, 4},
		{"Dupla negacao", "~~P", "P", []string{"P"}, 2},
		{"Contrapositiva", "P -> Q", "~Q -> ~P", []string{"P", "Q"}, 4},
		{"Bicondicional", "P <-> Q", "(P -> Q) ^ (Q -> P)", []string{"P", "Q"}, 4},
		{"Absorcao unificando variaveis", "P", "P v (P ^ Q)", []string{"P", "Q"}, 4},
		{"Distributiva 3 variaveis", "P ^ (Q v R)", "(P ^ Q) v (P ^ R)", []string{"P", "Q", "R"}, 8},
	}

	for _, tc := range tests {
		t.Run(tc.name, func(t *testing.T) {
			res, err := engine.ValidateEquivalence(ctx, tc.formulaA, tc.formulaB)
			if err != nil {
				t.Fatalf("erro inesperado: %v", err)
			}
			if !res.Equivalent {
				t.Fatalf("esperado Equivalent=true, obteve false com contraexemplo %v", res.Counterexample)
			}
			if res.Counterexample != nil {
				t.Errorf("esperado Counterexample nil, obteve %v", res.Counterexample)
			}
			if !reflect.DeepEqual(res.Variables, tc.vars) {
				t.Errorf("Variables = %v; esperado %v", res.Variables, tc.vars)
			}
			if len(res.TruthTable) != tc.rows {
				t.Errorf("len(TruthTable) = %d; esperado %d", len(res.TruthTable), tc.rows)
			}
		})
	}
}

func TestValidateEquivalenceCounterexample(t *testing.T) {
	engine := domain.NewLogicEngine(nil)
	res, err := engine.ValidateEquivalence(context.Background(), "P -> Q", "Q -> P")
	if err != nil {
		t.Fatalf("erro inesperado: %v", err)
	}
	if res.Equivalent {
		t.Fatalf("esperado Equivalent=false")
	}

	expectedCounterexample := map[string]bool{"P": true, "Q": false}
	if !reflect.DeepEqual(res.Counterexample, expectedCounterexample) {
		t.Errorf("Counterexample = %v; esperado %v", res.Counterexample, expectedCounterexample)
	}
}

func TestValidateEquivalenceTooManyVariables(t *testing.T) {
	engine := domain.NewLogicEngine(nil)
	_, err := engine.ValidateEquivalence(
		context.Background(),
		"A ^ B ^ C ^ D ^ E ^ F ^ G ^ H ^ I ^ J ^ K ^ L ^ M",
		"A",
	)
	if !errors.Is(err, domain.ErrTooManyVariables) {
		t.Fatalf("esperado ErrTooManyVariables, obteve %v", err)
	}
}

func TestRespectsContextDeadlineAndCancellation(t *testing.T) {
	t.Run("contexto cancelado no Parse", func(t *testing.T) {
		engine := domain.NewLogicEngine(nil)
		ctx, cancel := context.WithCancel(context.Background())
		cancel()

		_, err := engine.Parse(ctx, "(P & Q) -> R")
		if !errors.Is(err, context.Canceled) {
			t.Fatalf("esperado context.Canceled, obteve %v", err)
		}
	})

	t.Run("prazo expirado durante avaliacao da tabela-verdade", func(t *testing.T) {
		engine := domain.NewLogicEngine(slowObserver{delay: 15 * time.Millisecond})
		ctx, cancel := context.WithTimeout(context.Background(), 20*time.Millisecond)
		defer cancel()

		_, err := engine.ValidateEquivalence(ctx, "P ^ (Q v R)", "(P ^ Q) v (P ^ R)")
		if !errors.Is(err, context.DeadlineExceeded) {
			t.Fatalf("esperado context.DeadlineExceeded, obteve %v", err)
		}
	})
}
