-- Statut éditorial d'un film : seuls les films PUBLIE sont visibles dans le catalogue public.
-- Valeur par défaut PUBLIE : les films déjà présents (dont les films de démonstration) restent visibles.
ALTER TABLE film
    ADD COLUMN statut VARCHAR(20) NOT NULL DEFAULT 'PUBLIE'
        CHECK (statut IN ('BROUILLON', 'PUBLIE'));

CREATE INDEX idx_film_statut ON film (statut);
