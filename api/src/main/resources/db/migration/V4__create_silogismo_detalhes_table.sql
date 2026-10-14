-- Migração V4: Criação das tabelas de Redução e Detalhe do Silogismo (1:1) e separação de modo e valido
CREATE TABLE silogismo_reducoes (
    id BIGSERIAL PRIMARY KEY,
    modo VARCHAR(40) NOT NULL,
    nome VARCHAR(60) NOT NULL,
    proposicao_maior_id BIGINT NOT NULL,
    proposicao_menor_id BIGINT NOT NULL,
    conclusao_id BIGINT NOT NULL,
    CONSTRAINT fk_reducao_proposicao_maior FOREIGN KEY (proposicao_maior_id) REFERENCES proposicoes(id) ON DELETE CASCADE,
    CONSTRAINT fk_reducao_proposicao_menor FOREIGN KEY (proposicao_menor_id) REFERENCES proposicoes(id) ON DELETE CASCADE,
    CONSTRAINT fk_reducao_conclusao FOREIGN KEY (conclusao_id) REFERENCES proposicoes(id) ON DELETE CASCADE
);

CREATE TABLE silogismo_detalhes (
    id BIGSERIAL PRIMARY KEY,
    modo VARCHAR(40),
    nome VARCHAR(60),
    valido BOOLEAN NOT NULL,
    reduzivel BOOLEAN NOT NULL,
    reducao_id BIGINT UNIQUE,
    CONSTRAINT fk_detalhe_reducao FOREIGN KEY (reducao_id) REFERENCES silogismo_reducoes(id) ON DELETE CASCADE,
    CONSTRAINT chk_detalhe_reducao_obrigatoria CHECK (NOT reduzivel OR reducao_id IS NOT NULL)
);

CREATE INDEX idx_silogismo_detalhes_modo ON silogismo_detalhes(modo);

ALTER TABLE silogismos
    DROP COLUMN modo,
    DROP COLUMN valido,
    ADD COLUMN detalhe_id BIGINT UNIQUE,
    ADD CONSTRAINT fk_silogismo_detalhe FOREIGN KEY (detalhe_id) REFERENCES silogismo_detalhes(id) ON DELETE SET NULL;
