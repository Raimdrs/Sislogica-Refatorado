-- Migração V3: Criação da tabela de Proposições e vínculo com Silogismos
CREATE TABLE proposicoes (
    id BIGSERIAL PRIMARY KEY,
    quantificador VARCHAR(1) NOT NULL CHECK (quantificador IN ('A', 'E', 'I', 'O')),
    sujeito VARCHAR(1) NOT NULL CHECK (sujeito IN ('T', 't', 'M')),
    predicado VARCHAR(1) NOT NULL CHECK (predicado IN ('T', 't', 'M')),
    transliteracao TEXT
);

TRUNCATE TABLE silogismos CASCADE;

ALTER TABLE silogismos
    DROP COLUMN premissa_maior,
    DROP COLUMN premissa_menor,
    DROP COLUMN conclusao,
    ADD COLUMN premissa_maior_id BIGINT NOT NULL,
    ADD COLUMN premissa_menor_id BIGINT NOT NULL,
    ADD COLUMN conclusao_id BIGINT NOT NULL,
    ADD CONSTRAINT fk_silogismo_premissa_maior FOREIGN KEY (premissa_maior_id) REFERENCES proposicoes(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_silogismo_premissa_menor FOREIGN KEY (premissa_menor_id) REFERENCES proposicoes(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_silogismo_conclusao FOREIGN KEY (conclusao_id) REFERENCES proposicoes(id) ON DELETE CASCADE;
