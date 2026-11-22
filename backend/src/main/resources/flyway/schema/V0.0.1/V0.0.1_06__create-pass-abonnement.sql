CREATE SEQUENCE IF NOT EXISTS pass_id_seq START 1 INCREMENT 50;

CREATE TABLE pass (
    id                 BIGINT PRIMARY KEY,
    code               VARCHAR(30) NOT NULL UNIQUE,
    nom                VARCHAR(80) NOT NULL,
    duree_heures       INT         NOT NULL,
    prix_fcfa          INT         NOT NULL,
    ecrans_max         SMALLINT    NOT NULL DEFAULT 1,
    qualite_max        VARCHAR(20) NOT NULL DEFAULT 'HD_1080P',
    etiquette          VARCHAR(50),
    description        VARCHAR(255),
    actif              BOOLEAN     NOT NULL DEFAULT TRUE,
    create_by          VARCHAR(255),
    create_at          TIMESTAMP,
    update_by          VARCHAR(255),
    update_at          TIMESTAMP,
    version            BIGINT NOT NULL DEFAULT 0
);

CREATE SEQUENCE IF NOT EXISTS abonnement_id_seq START 1 INCREMENT 50;

CREATE TABLE abonnement (
    id                 BIGINT PRIMARY KEY,
    utilisateur_id     BIGINT      NOT NULL REFERENCES utilisateur (id),
    pass_id            BIGINT      NOT NULL REFERENCES pass (id),
    date_debut         TIMESTAMP   NOT NULL,
    date_fin           TIMESTAMP   NOT NULL,
    statut             VARCHAR(20) NOT NULL CHECK (statut IN ('ACTIF', 'EXPIRE', 'ANNULE')),
    create_by          VARCHAR(255),
    create_at          TIMESTAMP,
    update_by          VARCHAR(255),
    update_at          TIMESTAMP,
    version            BIGINT NOT NULL DEFAULT 0
);
CREATE INDEX idx_abonnement_utilisateur_fin ON abonnement (utilisateur_id, date_fin);
