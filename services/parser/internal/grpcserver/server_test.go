package grpcserver_test

import (
	"context"
	"net"
	"reflect"
	"testing"
	"time"

	"google.golang.org/grpc"
	"google.golang.org/grpc/codes"
	"google.golang.org/grpc/credentials/insecure"
	"google.golang.org/grpc/health/grpc_health_v1"
	"google.golang.org/grpc/status"
	"google.golang.org/grpc/test/bufconn"

	parserv1 "github.com/sislogica/services/parser/gen/sislogica/parser/v1"
	"github.com/sislogica/services/parser/internal/domain"
	"github.com/sislogica/services/parser/internal/grpcserver"
)

const bufSize = 1024 * 1024

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

func startTestServer(t *testing.T, svc grpcserver.LogicService) *grpc.ClientConn {
	t.Helper()

	lis := bufconn.Listen(bufSize)
	srv, _ := grpcserver.NewServer(svc)

	go func() {
		_ = srv.Serve(lis)
	}()

	conn, err := grpc.NewClient(
		"passthrough://bufnet",
		grpc.WithContextDialer(func(ctx context.Context, _ string) (net.Conn, error) {
			return lis.DialContext(ctx)
		}),
		grpc.WithTransportCredentials(insecure.NewCredentials()),
	)
	if err != nil {
		srv.Stop()
		t.Fatalf("falha ao conectar no bufconn: %v", err)
	}

	t.Cleanup(func() {
		_ = conn.Close()
		srv.GracefulStop()
		_ = lis.Close()
	})

	return conn
}

func TestParseExpressionOverGRPC(t *testing.T) {
	conn := startTestServer(t, domain.NewLogicEngine(nil))
	client := parserv1.NewLogicParserServiceClient(conn)

	ctx, cancel := context.WithTimeout(context.Background(), 2*time.Second)
	defer cancel()

	t.Run("expressao valida retorna variaveis e AST JSON", func(t *testing.T) {
		resp, err := client.ParseExpression(ctx, &parserv1.ParseExpressionRequest{
			Expression: "(P & Q) -> R",
		})
		if err != nil {
			t.Fatalf("ParseExpression falhou: %v", err)
		}
		if !resp.GetValid() {
			t.Errorf("esperado Valid=true")
		}
		if !reflect.DeepEqual(resp.GetVariables(), []string{"P", "Q", "R"}) {
			t.Errorf("Variables = %v; esperado [P Q R]", resp.GetVariables())
		}
		if resp.GetAstJson() == "" {
			t.Errorf("AstJson não deveria ser vazio")
		}
	})

	t.Run("expressao malformada retorna InvalidArgument", func(t *testing.T) {
		_, err := client.ParseExpression(ctx, &parserv1.ParseExpressionRequest{
			Expression: "(P -> Q",
		})
		if status.Code(err) != codes.InvalidArgument {
			t.Fatalf("esperado codes.InvalidArgument, obteve %v (%v)", status.Code(err), err)
		}
	})
}

func TestValidateEquivalenceOverGRPC(t *testing.T) {
	conn := startTestServer(t, domain.NewLogicEngine(nil))
	client := parserv1.NewLogicParserServiceClient(conn)

	ctx, cancel := context.WithTimeout(context.Background(), 2*time.Second)
	defer cancel()

	t.Run("Lei de De Morgan retorna equivalent=true", func(t *testing.T) {
		resp, err := client.ValidateEquivalence(ctx, &parserv1.ValidateEquivalenceRequest{
			FormulaA: "~(P ^ Q)",
			FormulaB: "~P v ~Q",
		})
		if err != nil {
			t.Fatalf("ValidateEquivalence falhou: %v", err)
		}
		if !resp.GetEquivalent() {
			t.Errorf("esperado Equivalent=true")
		}
		if len(resp.GetCounterexample()) != 0 {
			t.Errorf("esperado Counterexample vazio, obteve %v", resp.GetCounterexample())
		}
		if len(resp.GetTruthTable()) != 4 {
			t.Errorf("esperado 4 linhas na tabela-verdade, obteve %d", len(resp.GetTruthTable()))
		}
	})

	t.Run("formulas nao equivalentes retornam contraexemplo", func(t *testing.T) {
		resp, err := client.ValidateEquivalence(ctx, &parserv1.ValidateEquivalenceRequest{
			FormulaA: "P -> Q",
			FormulaB: "Q -> P",
		})
		if err != nil {
			t.Fatalf("ValidateEquivalence falhou: %v", err)
		}
		if resp.GetEquivalent() {
			t.Errorf("esperado Equivalent=false")
		}
		expected := map[string]bool{"P": true, "Q": false}
		if !reflect.DeepEqual(resp.GetCounterexample(), expected) {
			t.Errorf("Counterexample = %v; esperado %v", resp.GetCounterexample(), expected)
		}
	})

	t.Run("formula invalida retorna InvalidArgument", func(t *testing.T) {
		_, err := client.ValidateEquivalence(ctx, &parserv1.ValidateEquivalenceRequest{
			FormulaA: "P ^^ Q",
			FormulaB: "P ^ Q",
		})
		if status.Code(err) != codes.InvalidArgument {
			t.Fatalf("esperado codes.InvalidArgument, obteve %v (%v)", status.Code(err), err)
		}
	})
}

func TestValidateEquivalenceDeadlineExceededOverGRPC(t *testing.T) {
	conn := startTestServer(t, domain.NewLogicEngine(slowObserver{delay: 20 * time.Millisecond}))
	client := parserv1.NewLogicParserServiceClient(conn)

	ctx, cancel := context.WithTimeout(context.Background(), 25*time.Millisecond)
	defer cancel()

	_, err := client.ValidateEquivalence(ctx, &parserv1.ValidateEquivalenceRequest{
		FormulaA: "P ^ (Q v R)",
		FormulaB: "(P ^ Q) v (P ^ R)",
	})
	if status.Code(err) != codes.DeadlineExceeded {
		t.Fatalf("esperado codes.DeadlineExceeded, obteve %v (%v)", status.Code(err), err)
	}
}

func TestHealthCheckServing(t *testing.T) {
	conn := startTestServer(t, domain.NewLogicEngine(nil))
	healthClient := grpc_health_v1.NewHealthClient(conn)

	ctx, cancel := context.WithTimeout(context.Background(), 2*time.Second)
	defer cancel()

	for _, svc := range []string{"", grpcserver.ServiceName} {
		resp, err := healthClient.Check(ctx, &grpc_health_v1.HealthCheckRequest{Service: svc})
		if err != nil {
			t.Fatalf("Health.Check(%q) falhou: %v", svc, err)
		}
		if resp.GetStatus() != grpc_health_v1.HealthCheckResponse_SERVING {
			t.Errorf("Health.Check(%q) = %v; esperado SERVING", svc, resp.GetStatus())
		}
	}
}
