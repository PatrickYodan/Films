CREATE SEQUENCE IF NOT EXISTS utilisateur_id_seq START 1 INCREMENT 50;

CREATE TABLE utilisateur (
    id                 BIGINT PRIMARY KEY,
    nom                VARCHAR(150) NOT NULL,
    email              VARCHAR(255) NOT NULL UNIQUE,
    telephone          VARCHAR(20),
    mot_de_passe_hash  VARCHAR(255) NOT NULL,
    role               VARCHAR(20)  NOT NULL CHECK (role IN ('ABONNE', 'ADMIN')),
    create_by          VARCHAR(255),
    create_at          TIMESTAMP,
    update_by          VARCHAR(255),
    update_at          TIMESTAMP,
    version            BIGINT NOT NULL DEFAULT 0
);
