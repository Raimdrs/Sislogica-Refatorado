package main

import (
	"reflect"
	"testing"
)

func TestExtractVariables(t *testing.T) {
	tests := []struct {
		input    string
		expected []string
	}{
		{"(P & Q) -> R", []string{"P", "Q", "R"}},
		{"~P | P", []string{"P"}},
		{"A -> (B & C)", []string{"A", "B", "C"}},
	}

	for _, tt := range tests {
		got := ExtractVariables(tt.input)
		if !reflect.DeepEqual(got, tt.expected) {
			t.Errorf("ExtractVariables(%q) = %v; esperado %v", tt.input, got, tt.expected)
		}
	}
}

func TestValidateParentheses(t *testing.T) {
	valid := []string{"(P & Q)", "((P -> Q) & (Q -> R))", "P", "~(P | Q)"}
	invalid := []string{"(P & Q", "P & Q)", ")P & Q(", "((P)"}

	for _, s := range valid {
		if !ValidateParentheses(s) {
			t.Errorf("ValidateParentheses(%q) esperado true, obteve false", s)
		}
	}

	for _, s := range invalid {
		if ValidateParentheses(s) {
			t.Errorf("ValidateParentheses(%q) esperado false, obteve true", s)
		}
	}
}

func TestParseProposition(t *testing.T) {
	_, err := ParseProposition("")
	if err == nil {
		t.Errorf("ParseProposition com string vazia deveria retornar erro")
	}

	res, err := ParseProposition("(P & Q) -> R")
	if err != nil {
		t.Fatalf("Erro inesperado: %v", err)
	}

	if !res.Valid {
		t.Errorf("Esperado resultado válido")
	}
	if len(res.Variables) != 3 {
		t.Errorf("Esperado 3 variáveis, obteve %d", len(res.Variables))
	}
}
