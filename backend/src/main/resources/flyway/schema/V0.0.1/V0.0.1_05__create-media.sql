CREATE SEQUENCE IF NOT EXISTS fichier_video_id_seq START 1 INCREMENT 50;

CREATE TABLE fichier_video (
    id            BIGINT PRIMARY KEY,
    film_id       BIGINT       NOT NULL REFERENCES film (id) ON DELETE CASCADE,
    qualite       VARCHAR(20)  NOT NULL CHECK (qualite IN ('ECO_240P', 'STANDARD_480P', 'HD_1080P')),
    chemin_hls    VARCHAR(500) NOT NULL,
    taille_octets BIGINT,
    UNIQUE (film_id, qualite)
);

CREATE SEQUENCE IF NOT EXISTS sous_titre_id_seq START 1 INCREMENT 50;

CREATE TABLE sous_titre (
    id              BIGINT PRIMARY KEY,
    film_id         BIGINT       NOT NULL REFERENCES film (id) ON DELETE CASCADE,
    langue          VARCHAR(10)  NOT NULL,
    libelle         VARCHAR(50)  NOT NULL,
    fichier_vtt_url VARCHAR(500) NOT NULL
);
