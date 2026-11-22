CREATE SEQUENCE IF NOT EXISTS paiement_id_seq START 1 INCREMENT 50;

CREATE TABLE paiement (
    id                  BIGINT PRIMARY KEY,
    reference           VARCHAR(40) NOT NULL UNIQUE,
    utilisateur_id      BIGINT      NOT NULL REFERENCES utilisateur (id),
    pass_id             BIGINT      NOT NULL REFERENCES pass (id),
    abonnement_id       BIGINT REFERENCES abonnement (id),
    moyen               VARCHAR(20) NOT NULL CHECK (moyen IN ('ORANGE_MONEY', 'MTN_MOMO', 'WAVE', 'MOOV_MONEY', 'CARTE')),
    telephone           VARCHAR(20),
    montant_fcfa        INT         NOT NULL,
    statut              VARCHAR(20) NOT NULL CHECK (statut IN ('EN_ATTENTE', 'CONFIRME', 'ECHOUE', 'EXPIRE')),
    reference_operateur VARCHAR(100),
    confirme_le         TIMESTAMP,
    create_by          VARCHAR(255),
    create_at          TIMESTAMP,
    update_by          VARCHAR(255),
    update_at          TIMESTAMP,
    version            BIGINT NOT NULL DEFAULT 0
);
CREATE INDEX idx_paiement_reference_operateur ON paiement (reference_operateur);
CREATE INDEX idx_paiement_statut ON paiement (statut);
