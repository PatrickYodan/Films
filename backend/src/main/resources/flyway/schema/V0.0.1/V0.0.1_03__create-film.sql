CREATE SEQUENCE IF NOT EXISTS film_id_seq START 1 INCREMENT 50;

CREATE TABLE film (
    id                 BIGINT PRIMARY KEY,
    titre              VARCHAR(255) NOT NULL,
    slug               VARCHAR(255) NOT NULL UNIQUE,
    synopsis_court     TEXT,
    synopsis           TEXT,
    annee              SMALLINT,
    duree_minutes      SMALLINT,
    classification_age VARCHAR(10),
    type               VARCHAR(10)  NOT NULL DEFAULT 'FILM' CHECK (type IN ('FILM', 'SERIE')),
    gratuit            BOOLEAN      NOT NULL DEFAULT FALSE,
    exclusivite        BOOLEAN      NOT NULL DEFAULT FALSE,
    telechargeable     BOOLEAN      NOT NULL DEFAULT FALSE,
    affiche_url        VARCHAR(500),
    bandeau_url        VARCHAR(500),
    bande_annonce_url  VARCHAR(500),
    date_sortie        DATE,
    create_by          VARCHAR(255),
    create_at          TIMESTAMP,
    update_by          VARCHAR(255),
    update_at          TIMESTAMP,
    version            BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_film_gratuit ON film (gratuit);
CREATE INDEX idx_film_date_sortie ON film (date_sortie DESC);
CREATE INDEX idx_film_recherche ON film
    USING GIN (to_tsvector('french', coalesce(titre, '') || ' ' || coalesce(synopsis, '')));

CREATE TABLE film_pays (
    film_id   BIGINT  NOT NULL REFERENCES film (id) ON DELETE CASCADE,
    pays_code CHAR(2) NOT NULL REFERENCES pays (code),
    PRIMARY KEY (film_id, pays_code)
);
CREATE INDEX idx_film_pays_pays ON film_pays (pays_code);

CREATE TABLE film_genre (
    film_id  BIGINT NOT NULL REFERENCES film (id) ON DELETE CASCADE,
    genre_id BIGINT NOT NULL REFERENCES genre (id),
    PRIMARY KEY (film_id, genre_id)
);
