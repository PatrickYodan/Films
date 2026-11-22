-- Photo de profil, stockée à part pour ne pas alourdir le chargement de l'utilisateur à chaque requête.
CREATE TABLE utilisateur_photo (
    utilisateur_id BIGINT PRIMARY KEY REFERENCES utilisateur (id) ON DELETE CASCADE,
    contenu        BYTEA       NOT NULL,
    type_mime      VARCHAR(50) NOT NULL,
    maj_le         TIMESTAMP   NOT NULL DEFAULT now()
);
