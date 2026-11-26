package main

import (
	"context"
	"log/slog"
	"net"
	"os"
	"os/signal"
	"syscall"

	"github.com/sislogica/services/parser/internal/domain"
	"github.com/sislogica/services/parser/internal/grpcserver"
)

func main() {
	logger := slog.New(slog.NewJSONHandler(os.Stdout, nil))

	port := os.Getenv("GRPC_PORT")
	if port == "" {
		port = "50051"
	}

	lis, err := net.Listen("tcp", ":"+port)
	if err != nil {
		logger.Error("falha ao abrir porta gRPC", "port", port, "error", err)
		os.Exit(1)
	}

	engine := domain.NewLogicEngine(nil)
	srv, healthSrv := grpcserver.NewServer(engine)

	ctx, stop := signal.NotifyContext(context.Background(), os.Interrupt, syscall.SIGTERM)
	defer stop()

	serveErr := make(chan error, 1)
	go func() {
		logger.Info("Sílógica Logic Parser gRPC Service iniciado", "port", port, "service", grpcserver.ServiceName)
		serveErr <- srv.Serve(lis)
	}()

	select {
	case err := <-serveErr:
		if err != nil {
			logger.Error("servidor gRPC encerrado com erro", "error", err)
			os.Exit(1)
		}
	case <-ctx.Done():
		logger.Info("sinal de encerramento recebido; iniciando GracefulStop")
		healthSrv.Shutdown()
		srv.GracefulStop()
		logger.Info("servidor gRPC encerrado graciosamente")
	}
}
