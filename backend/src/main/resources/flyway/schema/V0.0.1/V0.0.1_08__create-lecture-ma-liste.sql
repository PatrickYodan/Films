CREATE TABLE favori (
    utilisateur_id BIGINT    NOT NULL REFERENCES utilisateur (id) ON DELETE CASCADE,
    film_id        BIGINT    NOT NULL REFERENCES film (id) ON DELETE CASCADE,
    ajoute_le      TIMESTAMP NOT NULL DEFAULT now(),
    PRIMARY KEY (utilisateur_id, film_id)
);

CREATE TABLE visionnage (
    utilisateur_id    BIGINT    NOT NULL REFERENCES utilisateur (id) ON DELETE CASCADE,
    film_id           BIGINT    NOT NULL REFERENCES film (id) ON DELETE CASCADE,
    position_secondes INT       NOT NULL DEFAULT 0,
    qualite           VARCHAR(20),
    termine           BOOLEAN   NOT NULL DEFAULT FALSE,
    maj_le            TIMESTAMP NOT NULL DEFAULT now(),
    PRIMARY KEY (utilisateur_id, film_id)
);

CREATE TABLE session_lecture (
    id                VARCHAR(36) PRIMARY KEY,
    utilisateur_id    BIGINT    NOT NULL REFERENCES utilisateur (id) ON DELETE CASCADE,
    film_id           BIGINT    NOT NULL REFERENCES film (id) ON DELETE CASCADE,
    appareil          VARCHAR(255),
    derniere_activite TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_session_lecture_utilisateur ON session_lecture (utilisateur_id, derniere_activite);
