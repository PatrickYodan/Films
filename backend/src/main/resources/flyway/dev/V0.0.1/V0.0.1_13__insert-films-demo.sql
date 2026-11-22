-- Films de démonstration (titres et synopsis fictifs) pour visualiser les écrans.
-- Les affiches sont servies par le frontend (frontend/src/assets/img/films).

INSERT INTO film (id, titre, slug, synopsis_court, synopsis, annee, duree_minutes, classification_age, type, gratuit, exclusivite, telechargeable, affiche_url, bandeau_url, date_sortie, create_by, create_at)
VALUES (nextval('film_id_seq'), 'L''Ombre du baobab', 'l-ombre-du-baobab', 'Une héritière revient au village pour sauver l''arbre de ses ancêtres.', 'Après quinze ans à Paris, Awa revient dans son village de l''Ouest ivoirien pour l''enterrement de sa grand-mère. Elle découvre qu''un promoteur veut abattre le baobab sacré autour duquel toute la communauté s''est construite. Entre secrets de famille et choix d''avenir, elle devra décider à quel monde elle appartient.', 2025, 112, '13+', 'FILM', FALSE, TRUE, TRUE, 'assets/img/films/l-ombre-du-baobab-affiche.svg', 'assets/img/films/l-ombre-du-baobab-bandeau.svg', DATE '2025-10-03', 'FLYWAY', now());
INSERT INTO film_pays (film_id, pays_code) SELECT id, 'CI' FROM film WHERE slug = 'l-ombre-du-baobab';
INSERT INTO film_genre (film_id, genre_id) SELECT f.id, g.id FROM film f, genre g WHERE f.slug = 'l-ombre-du-baobab' AND g.nom = 'Drame';

INSERT INTO film (id, titre, slug, synopsis_court, synopsis, annee, duree_minutes, classification_age, type, gratuit, exclusivite, telechargeable, affiche_url, bandeau_url, date_sortie, create_by, create_at)
VALUES (nextval('film_id_seq'), 'Les Rues d''Abidjan', 'les-rues-d-abidjan', 'Trois amis, un gbaka et une journée pour rembourser une dette.', 'Koffi, Bamba et Ange ont jusqu''au coucher du soleil pour réunir l''argent qu''ils doivent au redoutable « Président » du quartier. À bord d''un gbaka à bout de souffle, ils traversent Abidjan de Yopougon à Cocody, enchaînant combines et rencontres improbables.', 2024, 95, 'Tous', 'FILM', TRUE, FALSE, FALSE, 'assets/img/films/les-rues-d-abidjan-affiche.svg', 'assets/img/films/les-rues-d-abidjan-bandeau.svg', DATE '2024-06-14', 'FLYWAY', now());
INSERT INTO film_pays (film_id, pays_code) SELECT id, 'CI' FROM film WHERE slug = 'les-rues-d-abidjan';
INSERT INTO film_genre (film_id, genre_id) SELECT f.id, g.id FROM film f, genre g WHERE f.slug = 'les-rues-d-abidjan' AND g.nom = 'Comédie';

INSERT INTO film (id, titre, slug, synopsis_court, synopsis, annee, duree_minutes, classification_age, type, gratuit, exclusivite, telechargeable, affiche_url, bandeau_url, date_sortie, create_by, create_at)
VALUES (nextval('film_id_seq'), 'Saison des pluies', 'saison-des-pluies', 'À Ziguinchor, un amour naît le temps d''un hivernage.', 'Fatou, institutrice à Ziguinchor, croise Ibrahima, ingénieur venu réparer le pont emporté par les crues. Le temps d''une saison des pluies, ils apprennent à se connaître, alors que chacun porte une promesse faite à d''autres.', 2025, 104, '13+', 'FILM', FALSE, FALSE, TRUE, 'assets/img/films/saison-des-pluies-affiche.svg', 'assets/img/films/saison-des-pluies-bandeau.svg', DATE '2025-08-22', 'FLYWAY', now());
INSERT INTO film_pays (film_id, pays_code) SELECT id, 'SN' FROM film WHERE slug = 'saison-des-pluies';
INSERT INTO film_genre (film_id, genre_id) SELECT f.id, g.id FROM film f, genre g WHERE f.slug = 'saison-des-pluies' AND g.nom = 'Romance';
INSERT INTO film_genre (film_id, genre_id) SELECT f.id, g.id FROM film f, genre g WHERE f.slug = 'saison-des-pluies' AND g.nom = 'Drame';

INSERT INTO film (id, titre, slug, synopsis_court, synopsis, annee, duree_minutes, classification_age, type, gratuit, exclusivite, telechargeable, affiche_url, bandeau_url, date_sortie, create_by, create_at)
VALUES (nextval('film_id_seq'), 'Lagos Express', 'lagos-express', 'Un coursier à moto se retrouve au cœur d''un complot.', 'Tunde livre des colis à moto dans les embouteillages de Lagos. Le jour où il accepte une course trop bien payée, il devient la cible de deux gangs rivaux et d''une policière déterminée. Il a une nuit pour comprendre ce qu''il transporte.', 2025, 118, '16+', 'FILM', FALSE, FALSE, FALSE, 'assets/img/films/lagos-express-affiche.svg', 'assets/img/films/lagos-express-bandeau.svg', DATE '2025-09-12', 'FLYWAY', now());
INSERT INTO film_pays (film_id, pays_code) SELECT id, 'NG' FROM film WHERE slug = 'lagos-express';
INSERT INTO film_genre (film_id, genre_id) SELECT f.id, g.id FROM film f, genre g WHERE f.slug = 'lagos-express' AND g.nom = 'Action';
INSERT INTO film_genre (film_id, genre_id) SELECT f.id, g.id FROM film f, genre g WHERE f.slug = 'lagos-express' AND g.nom = 'Thriller';

INSERT INTO film (id, titre, slug, synopsis_court, synopsis, annee, duree_minutes, classification_age, type, gratuit, exclusivite, telechargeable, affiche_url, bandeau_url, date_sortie, create_by, create_at)
VALUES (nextval('film_id_seq'), 'Le Griot de Ségou', 'le-griot-de-segou', 'Portrait d''un gardien de la mémoire mandingue.', 'Pendant un an, la caméra suit Mamadou, griot de Ségou, qui transmet à ses petits-enfants les récits de l''empire du Mali. Un documentaire sur la parole, la musique et ce qu''il reste à préserver.', 2023, 78, 'Tous', 'FILM', TRUE, FALSE, TRUE, 'assets/img/films/le-griot-de-segou-affiche.svg', 'assets/img/films/le-griot-de-segou-bandeau.svg', DATE '2023-11-30', 'FLYWAY', now());
INSERT INTO film_pays (film_id, pays_code) SELECT id, 'ML' FROM film WHERE slug = 'le-griot-de-segou';
INSERT INTO film_genre (film_id, genre_id) SELECT f.id, g.id FROM film f, genre g WHERE f.slug = 'le-griot-de-segou' AND g.nom = 'Documentaire';

INSERT INTO film (id, titre, slug, synopsis_court, synopsis, annee, duree_minutes, classification_age, type, gratuit, exclusivite, telechargeable, affiche_url, bandeau_url, date_sortie, create_by, create_at)
VALUES (nextval('film_id_seq'), 'Kumasi Blues', 'kumasi-blues', 'Une chanteuse de highlife et un comptable que tout oppose.', 'Esi chante tous les soirs dans un maquis de Kumasi. Kwame, comptable rigoureux, est envoyé pour fermer l''établissement endetté. Il a trois semaines, elle a un concert à préparer.', 2024, 101, 'Tous', 'FILM', FALSE, FALSE, FALSE, 'assets/img/films/kumasi-blues-affiche.svg', 'assets/img/films/kumasi-blues-bandeau.svg', DATE '2024-02-09', 'FLYWAY', now());
INSERT INTO film_pays (film_id, pays_code) SELECT id, 'GH' FROM film WHERE slug = 'kumasi-blues';
INSERT INTO film_genre (film_id, genre_id) SELECT f.id, g.id FROM film f, genre g WHERE f.slug = 'kumasi-blues' AND g.nom = 'Romance';
INSERT INTO film_genre (film_id, genre_id) SELECT f.id, g.id FROM film f, genre g WHERE f.slug = 'kumasi-blues' AND g.nom = 'Comédie';

INSERT INTO film (id, titre, slug, synopsis_court, synopsis, annee, duree_minutes, classification_age, type, gratuit, exclusivite, telechargeable, affiche_url, bandeau_url, date_sortie, create_by, create_at)
VALUES (nextval('film_id_seq'), 'Douala by Night', 'douala-by-night', 'Un chauffeur de taxi témoin d''un crime sur le port.', 'Chauffeur de nuit à Douala, Serge assiste à une scène qu''il n''aurait jamais dû voir près du port. Commence une longue nuit où chaque client peut être un allié ou une menace.', 2025, 99, '16+', 'FILM', FALSE, FALSE, FALSE, 'assets/img/films/douala-by-night-affiche.svg', 'assets/img/films/douala-by-night-bandeau.svg', DATE '2025-05-16', 'FLYWAY', now());
INSERT INTO film_pays (film_id, pays_code) SELECT id, 'CM' FROM film WHERE slug = 'douala-by-night';
INSERT INTO film_genre (film_id, genre_id) SELECT f.id, g.id FROM film f, genre g WHERE f.slug = 'douala-by-night' AND g.nom = 'Thriller';

INSERT INTO film (id, titre, slug, synopsis_court, synopsis, annee, duree_minutes, classification_age, type, gratuit, exclusivite, telechargeable, affiche_url, bandeau_url, date_sortie, create_by, create_at)
VALUES (nextval('film_id_seq'), 'Ouaga Cinéma', 'ouaga-cinema', 'Ceux qui font vivre le cinéma à Ouagadougou.', 'Projectionnistes, critiques, jeunes réalisateurs : rencontre avec celles et ceux qui font de Ouagadougou une capitale du cinéma africain, entre salles historiques et tournages au téléphone.', 2024, 85, 'Tous', 'FILM', TRUE, FALSE, FALSE, 'assets/img/films/ouaga-cinema-affiche.svg', 'assets/img/films/ouaga-cinema-bandeau.svg', DATE '2024-03-01', 'FLYWAY', now());
INSERT INTO film_pays (film_id, pays_code) SELECT id, 'BF' FROM film WHERE slug = 'ouaga-cinema';
INSERT INTO film_genre (film_id, genre_id) SELECT f.id, g.id FROM film f, genre g WHERE f.slug = 'ouaga-cinema' AND g.nom = 'Documentaire';

INSERT INTO film (id, titre, slug, synopsis_court, synopsis, annee, duree_minutes, classification_age, type, gratuit, exclusivite, telechargeable, affiche_url, bandeau_url, date_sortie, create_by, create_at)
VALUES (nextval('film_id_seq'), 'Le Fleuve aux mille voix', 'le-fleuve-aux-mille-voix', 'Le voyage d''une famille sur le fleuve Congo.', 'Pour rejoindre Kisangani, la famille Mbala embarque sur une barge où vivent des centaines de passagers. Pendant trois semaines de navigation, les destins se croisent et les secrets remontent à la surface.', 2023, 126, '13+', 'FILM', FALSE, FALSE, TRUE, 'assets/img/films/le-fleuve-aux-mille-voix-affiche.svg', 'assets/img/films/le-fleuve-aux-mille-voix-bandeau.svg', DATE '2023-09-08', 'FLYWAY', now());
INSERT INTO film_pays (film_id, pays_code) SELECT id, 'CD' FROM film WHERE slug = 'le-fleuve-aux-mille-voix';
INSERT INTO film_genre (film_id, genre_id) SELECT f.id, g.id FROM film f, genre g WHERE f.slug = 'le-fleuve-aux-mille-voix' AND g.nom = 'Drame';

INSERT INTO film (id, titre, slug, synopsis_court, synopsis, annee, duree_minutes, classification_age, type, gratuit, exclusivite, telechargeable, affiche_url, bandeau_url, date_sortie, create_by, create_at)
VALUES (nextval('film_id_seq'), 'Soweto Sunrise', 'soweto-sunrise', 'Une jeune coureuse vise les championnats nationaux.', 'Chaque matin avant l''aube, Lerato court dans les rues de Soweto. Repérée par un ancien entraîneur, elle doit concilier ses études, sa famille et un rêve qui la dépasse.', 2024, 109, '13+', 'FILM', FALSE, FALSE, FALSE, 'assets/img/films/soweto-sunrise-affiche.svg', 'assets/img/films/soweto-sunrise-bandeau.svg', DATE '2024-10-25', 'FLYWAY', now());
INSERT INTO film_pays (film_id, pays_code) SELECT id, 'ZA' FROM film WHERE slug = 'soweto-sunrise';
INSERT INTO film_genre (film_id, genre_id) SELECT f.id, g.id FROM film f, genre g WHERE f.slug = 'soweto-sunrise' AND g.nom = 'Drame';

INSERT INTO film (id, titre, slug, synopsis_court, synopsis, annee, duree_minutes, classification_age, type, gratuit, exclusivite, telechargeable, affiche_url, bandeau_url, date_sortie, create_by, create_at)
VALUES (nextval('film_id_seq'), 'Anansi et la Lune', 'anansi-et-la-lune', 'L''araignée rusée veut décrocher la Lune.', 'Anansi, l''araignée la plus malicieuse de la forêt, parie qu''elle peut rapporter la Lune au village. Une aventure animée pour toute la famille, inspirée des contes akan.', 2025, 82, 'Tous', 'FILM', TRUE, FALSE, TRUE, 'assets/img/films/anansi-et-la-lune-affiche.svg', 'assets/img/films/anansi-et-la-lune-bandeau.svg', DATE '2025-07-04', 'FLYWAY', now());
INSERT INTO film_pays (film_id, pays_code) SELECT id, 'GH' FROM film WHERE slug = 'anansi-et-la-lune';
INSERT INTO film_genre (film_id, genre_id) SELECT f.id, g.id FROM film f, genre g WHERE f.slug = 'anansi-et-la-lune' AND g.nom = 'Animation';

INSERT INTO film (id, titre, slug, synopsis_court, synopsis, annee, duree_minutes, classification_age, type, gratuit, exclusivite, telechargeable, affiche_url, bandeau_url, date_sortie, create_by, create_at)
VALUES (nextval('film_id_seq'), 'La Dot', 'la-dot', 'Négocier la dot peut tourner à la guerre des familles.', 'Yao veut épouser Adjoua. Encore faut-il que les deux familles s''entendent sur la dot. Entre oncles intransigeants et tantes stratèges, la négociation devient l''événement du quartier.', 2025, 97, 'Tous', 'FILM', FALSE, FALSE, FALSE, 'assets/img/films/la-dot-affiche.svg', 'assets/img/films/la-dot-bandeau.svg', DATE '2025-02-14', 'FLYWAY', now());
INSERT INTO film_pays (film_id, pays_code) SELECT id, 'CI' FROM film WHERE slug = 'la-dot';
INSERT INTO film_genre (film_id, genre_id) SELECT f.id, g.id FROM film f, genre g WHERE f.slug = 'la-dot' AND g.nom = 'Comédie';
INSERT INTO film_genre (film_id, genre_id) SELECT f.id, g.id FROM film f, genre g WHERE f.slug = 'la-dot' AND g.nom = 'Romance';

INSERT INTO film (id, titre, slug, synopsis_court, synopsis, annee, duree_minutes, classification_age, type, gratuit, exclusivite, telechargeable, affiche_url, bandeau_url, date_sortie, create_by, create_at)
VALUES (nextval('film_id_seq'), 'Dakar 2050', 'dakar-2050', 'Dans un Dakar futuriste, une hackeuse défie une multinationale.', 'En 2050, l''eau potable de Dakar est contrôlée par une seule entreprise. Aïssatou, hackeuse de génie, rassemble une équipe pour rendre l''eau à la ville.', 2025, 114, '13+', 'FILM', FALSE, FALSE, TRUE, 'assets/img/films/dakar-2050-affiche.svg', 'assets/img/films/dakar-2050-bandeau.svg', DATE '2025-11-01', 'FLYWAY', now());
INSERT INTO film_pays (film_id, pays_code) SELECT id, 'SN' FROM film WHERE slug = 'dakar-2050';
INSERT INTO film_genre (film_id, genre_id) SELECT f.id, g.id FROM film f, genre g WHERE f.slug = 'dakar-2050' AND g.nom = 'Action';

INSERT INTO film (id, titre, slug, synopsis_court, synopsis, annee, duree_minutes, classification_age, type, gratuit, exclusivite, telechargeable, affiche_url, bandeau_url, date_sortie, create_by, create_at)
VALUES (nextval('film_id_seq'), 'Le Dernier Marché', 'le-dernier-marche', 'Les commerçantes d''un marché menacé de démolition s''unissent.', 'Le marché central de Yaoundé doit laisser place à un centre commercial. Mama Odile, vendeuse de tissus depuis quarante ans, mobilise les commerçantes pour défendre leur place.', 2024, 93, 'Tous', 'FILM', FALSE, FALSE, FALSE, 'assets/img/films/le-dernier-marche-affiche.svg', 'assets/img/films/le-dernier-marche-bandeau.svg', DATE '2024-05-10', 'FLYWAY', now());
INSERT INTO film_pays (film_id, pays_code) SELECT id, 'CM' FROM film WHERE slug = 'le-dernier-marche';
INSERT INTO film_genre (film_id, genre_id) SELECT f.id, g.id FROM film f, genre g WHERE f.slug = 'le-dernier-marche' AND g.nom = 'Drame';

INSERT INTO film (id, titre, slug, synopsis_court, synopsis, annee, duree_minutes, classification_age, type, gratuit, exclusivite, telechargeable, affiche_url, bandeau_url, date_sortie, create_by, create_at)
VALUES (nextval('film_id_seq'), 'Nollywood Dreams', 'nollywood-dreams', 'Un tournage à petit budget où tout part de travers.', 'Chidi a dix jours et presque pas d''argent pour tourner son premier film. Acteurs capricieux, générateur en panne et producteur envahissant : rien ne se passe comme prévu.', 2023, 90, 'Tous', 'FILM', TRUE, FALSE, FALSE, 'assets/img/films/nollywood-dreams-affiche.svg', 'assets/img/films/nollywood-dreams-bandeau.svg', DATE '2023-12-15', 'FLYWAY', now());
INSERT INTO film_pays (film_id, pays_code) SELECT id, 'NG' FROM film WHERE slug = 'nollywood-dreams';
INSERT INTO film_genre (film_id, genre_id) SELECT f.id, g.id FROM film f, genre g WHERE f.slug = 'nollywood-dreams' AND g.nom = 'Comédie';

INSERT INTO film (id, titre, slug, synopsis_court, synopsis, annee, duree_minutes, classification_age, type, gratuit, exclusivite, telechargeable, affiche_url, bandeau_url, date_sortie, create_by, create_at)
VALUES (nextval('film_id_seq'), 'Akwaba', 'akwaba', 'Un jeune Burkinabè refait sa vie à Bouaké.', 'Arrivé à Bouaké sans rien, Issouf trouve du travail dans un atelier de couture. Entre l''accueil d''une famille et le mal du pays, il construit peu à peu un nouveau chez-lui.', 2024, 105, '13+', 'FILM', FALSE, FALSE, FALSE, 'assets/img/films/akwaba-affiche.svg', 'assets/img/films/akwaba-bandeau.svg', DATE '2024-08-30', 'FLYWAY', now());
INSERT INTO film_pays (film_id, pays_code) SELECT id, 'CI' FROM film WHERE slug = 'akwaba';
INSERT INTO film_pays (film_id, pays_code) SELECT id, 'BF' FROM film WHERE slug = 'akwaba';
INSERT INTO film_genre (film_id, genre_id) SELECT f.id, g.id FROM film f, genre g WHERE f.slug = 'akwaba' AND g.nom = 'Drame';
