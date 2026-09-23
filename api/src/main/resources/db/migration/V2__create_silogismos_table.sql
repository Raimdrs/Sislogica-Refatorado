-- Migração V2: Criação da tabela de Silogismos com relacionamento 1:N com Turmas
CREATE TABLE silogismos (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    premissa_maior TEXT NOT NULL,
    premissa_menor TEXT NOT NULL,
    conclusao TEXT NOT NULL,
    modo VARCHAR(40) NOT NULL,
    turma_id BIGINT NOT NULL,
    valido BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_silogismo_turma FOREIGN KEY (turma_id) REFERENCES turmas(id) ON DELETE CASCADE
);

CREATE INDEX idx_silogismos_turma_id ON silogismos(turma_id);
CREATE INDEX idx_silogismos_modo ON silogismos(modo);

