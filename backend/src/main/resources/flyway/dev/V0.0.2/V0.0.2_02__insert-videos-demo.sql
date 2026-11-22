-- Vidéos de démonstration (développement uniquement) : flux HLS publics de films libres de la Blender Foundation
--   Big Buck Bunny (CC BY 3.0), Elephants Dream (CC BY 2.5), Tears of Steel (CC BY 3.0).
-- Chaque flux contient plusieurs résolutions : le lecteur choisit celle qui correspond à la qualité demandée.
-- Les films de démo ne correspondent pas à ces vidéos : elles servent seulement à tester la lecture.
INSERT INTO fichier_video (id, film_id, qualite, chemin_hls, taille_octets)
SELECT nextval('fichier_video_id_seq'), f.id, q.qualite, v.url, NULL
FROM film f
JOIN (VALUES
    ('l-ombre-du-baobab', 'https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8'),
    ('les-rues-d-abidjan', 'https://playertest.longtailvideo.com/adaptive/elephants_dream_v4/index.m3u8'),
    ('saison-des-pluies', 'https://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8'),
    ('lagos-express', 'https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8'),
    ('le-griot-de-segou', 'https://playertest.longtailvideo.com/adaptive/elephants_dream_v4/index.m3u8'),
    ('kumasi-blues', 'https://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8'),
    ('douala-by-night', 'https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8'),
    ('ouaga-cinema', 'https://playertest.longtailvideo.com/adaptive/elephants_dream_v4/index.m3u8'),
    ('le-fleuve-aux-mille-voix', 'https://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8'),
    ('soweto-sunrise', 'https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8'),
    ('anansi-et-la-lune', 'https://playertest.longtailvideo.com/adaptive/elephants_dream_v4/index.m3u8'),
    ('la-dot', 'https://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8'),
    ('dakar-2050', 'https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8'),
    ('le-dernier-marche', 'https://playertest.longtailvideo.com/adaptive/elephants_dream_v4/index.m3u8'),
    ('nollywood-dreams', 'https://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8'),
    ('akwaba', 'https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8')
) AS v(slug, url) ON v.slug = f.slug
CROSS JOIN (VALUES ('ECO_240P'), ('STANDARD_480P'), ('HD_1080P')) AS q(qualite)
WHERE NOT EXISTS (SELECT 1 FROM fichier_video fv WHERE fv.film_id = f.id);
