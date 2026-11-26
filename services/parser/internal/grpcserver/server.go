package grpcserver

import (
	"context"
	"errors"

	"google.golang.org/grpc"
	"google.golang.org/grpc/codes"
	"google.golang.org/grpc/health"
	"google.golang.org/grpc/health/grpc_health_v1"
	"google.golang.org/grpc/reflection"
	"google.golang.org/grpc/status"

	parserv1 "github.com/sislogica/services/parser/gen/sislogica/parser/v1"
	"github.com/sislogica/services/parser/internal/domain"
)

// ServiceName é o nome qualificado do serviço registrado no gRPC e no Health Check.
const ServiceName = "sislogica.parser.v1.LogicParserService"

// LogicService define a interface mínima esperada pelo adaptador gRPC (declarada por quem usa).
type LogicService interface {
	Parse(ctx context.Context, expr string) (*domain.ParseResult, error)
	ValidateEquivalence(ctx context.Context, formulaA, formulaB string) (*domain.EquivalenceResult, error)
}

// Handler adapta requisições gRPC para o motor puro de domínio.
type Handler struct {
	parserv1.UnimplementedLogicParserServiceServer
	service LogicService
}

// NewHandler cria um adaptador gRPC conectado à implementação do domínio.
func NewHandler(service LogicService) *Handler {
	return &Handler{service: service}
}

// NewServer constrói um *grpc.Server configurado com o serviço LogicParserService,
// gRPC Health Check (SERVING) e gRPC Reflection.
func NewServer(service LogicService) (*grpc.Server, *health.Server) {
	srv := grpc.NewServer()
	parserv1.RegisterLogicParserServiceServer(srv, NewHandler(service))

	healthSrv := health.NewServer()
	healthSrv.SetServingStatus("", grpc_health_v1.HealthCheckResponse_SERVING)
	healthSrv.SetServingStatus(ServiceName, grpc_health_v1.HealthCheckResponse_SERVING)
	grpc_health_v1.RegisterHealthServer(srv, healthSrv)

	reflection.Register(srv)
	return srv, healthSrv
}

// ParseExpression traduz o RPC ParseExpression para domain.LogicEngine.Parse.
func (h *Handler) ParseExpression(ctx context.Context, req *parserv1.ParseExpressionRequest) (*parserv1.ParseExpressionResponse, error) {
	if req == nil {
		return nil, status.Error(codes.InvalidArgument, "requisição ParseExpression não pode ser nula")
	}

	res, err := h.service.Parse(ctx, req.GetExpression())
	if err != nil {
		return nil, mapDomainError(err)
	}

	return &parserv1.ParseExpressionResponse{
		Valid:     true,
		Variables: res.Variables,
		AstJson:   res.ASTJSON,
	}, nil
}

// ValidateEquivalence traduz o RPC ValidateEquivalence para domain.LogicEngine.ValidateEquivalence.
func (h *Handler) ValidateEquivalence(ctx context.Context, req *parserv1.ValidateEquivalenceRequest) (*parserv1.ValidateEquivalenceResponse, error) {
	if req == nil {
		return nil, status.Error(codes.InvalidArgument, "requisição ValidateEquivalence não pode ser nula")
	}

	res, err := h.service.ValidateEquivalence(ctx, req.GetFormulaA(), req.GetFormulaB())
	if err != nil {
		return nil, mapDomainError(err)
	}

	rows := make([]*parserv1.TruthTableRow, 0, len(res.TruthTable))
	for _, r := range res.TruthTable {
		rows = append(rows, &parserv1.TruthTableRow{
			Valuation:  r.Valuation,
			ResultA:    r.ResultA,
			ResultB:    r.ResultB,
			Equivalent: r.Equivalent,
		})
	}

	return &parserv1.ValidateEquivalenceResponse{
		Equivalent:     res.Equivalent,
		Variables:      res.Variables,
		Counterexample: res.Counterexample,
		TruthTable:     rows,
	}, nil
}

func mapDomainError(err error) error {
	switch {
	case errors.Is(err, domain.ErrEmptyExpression),
		errors.Is(err, domain.ErrUnbalancedParentheses),
		errors.Is(err, domain.ErrInvalidSyntax),
		errors.Is(err, domain.ErrTooManyVariables):
		return status.Error(codes.InvalidArgument, err.Error())
	case errors.Is(err, context.DeadlineExceeded):
		return status.Error(codes.DeadlineExceeded, err.Error())
	case errors.Is(err, context.Canceled):
		return status.Error(codes.Canceled, err.Error())
	default:
		return status.Error(codes.Internal, "erro interno ao processar fórmula proposicional")
	}
}
