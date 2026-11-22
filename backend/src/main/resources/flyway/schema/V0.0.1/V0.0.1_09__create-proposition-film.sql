CREATE SEQUENCE IF NOT EXISTS proposition_film_id_seq START 1 INCREMENT 50;

CREATE TABLE proposition_film (
    id                 BIGINT PRIMARY KEY,
    titre              VARCHAR(255) NOT NULL,
    contact            VARCHAR(255) NOT NULL,
    lien_visionnage    VARCHAR(500),
    message            TEXT,
    statut             VARCHAR(20)  NOT NULL DEFAULT 'RECUE' CHECK (statut IN ('RECUE', 'EN_ETUDE', 'ACCEPTEE', 'REFUSEE')),
    create_by          VARCHAR(255),
    create_at          TIMESTAMP,
    update_by          VARCHAR(255),
    update_at          TIMESTAMP,
    version            BIGINT NOT NULL DEFAULT 0
);
