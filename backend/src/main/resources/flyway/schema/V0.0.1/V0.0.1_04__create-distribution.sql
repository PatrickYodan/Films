CREATE SEQUENCE IF NOT EXISTS personne_id_seq START 1 INCREMENT 50;

CREATE TABLE personne (
    id        BIGINT PRIMARY KEY,
    nom       VARCHAR(150) NOT NULL,
    photo_url VARCHAR(500)
);

CREATE TABLE participation (
    film_id     BIGINT      NOT NULL REFERENCES film (id) ON DELETE CASCADE,
    personne_id BIGINT      NOT NULL REFERENCES personne (id),
    fonction    VARCHAR(20) NOT NULL CHECK (fonction IN ('ACTEUR', 'REALISATEUR', 'SCENARISTE', 'PRODUCTEUR')),
    role        VARCHAR(150),
    ordre       SMALLINT    NOT NULL DEFAULT 0,
    PRIMARY KEY (film_id, personne_id, fonction)
);
