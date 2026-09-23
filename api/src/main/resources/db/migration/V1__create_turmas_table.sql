-- Migração V1: Criação da tabela de Turmas (refatoração da entidade CLASSE do Silógica)
CREATE TABLE turmas (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(64) NOT NULL UNIQUE,
    professor VARCHAR(100) NOT NULL,
    descricao TEXT,
    criado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_turmas_codigo ON turmas(codigo);

