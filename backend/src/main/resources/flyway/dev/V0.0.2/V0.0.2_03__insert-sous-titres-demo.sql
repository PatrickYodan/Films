-- Sous-titres de démonstration (développement uniquement) : deux pistes, français et anglais,
-- servies par le frontend (frontend/src/assets/sous-titres). Elles servent à tester le choix des sous-titres
-- dans le lecteur ; leur texte ne correspond pas aux dialogues des vidéos de démonstration.
INSERT INTO sous_titre (id, film_id, langue, libelle, fichier_vtt_url)
SELECT nextval('sous_titre_id_seq'), f.id, p.langue, p.libelle, p.url
FROM film f
JOIN (VALUES
    ('l-ombre-du-baobab'),
    ('les-rues-d-abidjan'),
    ('saison-des-pluies'),
    ('lagos-express'),
    ('le-griot-de-segou'),
    ('kumasi-blues'),
    ('douala-by-night'),
    ('ouaga-cinema'),
    ('le-fleuve-aux-mille-voix'),
    ('soweto-sunrise'),
    ('anansi-et-la-lune'),
    ('la-dot'),
    ('dakar-2050'),
    ('le-dernier-marche'),
    ('nollywood-dreams'),
    ('akwaba')
) AS d(slug) ON d.slug = f.slug
CROSS JOIN (VALUES
    ('fr', 'Français', 'http://localhost:4200/assets/sous-titres/demo-fr.vtt'),
    ('en', 'English', 'http://localhost:4200/assets/sous-titres/demo-en.vtt')
) AS p(langue, libelle, url)
WHERE NOT EXISTS (SELECT 1 FROM sous_titre st WHERE st.film_id = f.id);
