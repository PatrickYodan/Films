-- Prix provisoires, à confirmer (« [PRIX] FCFA » dans la maquette)
INSERT INTO pass (id, code, nom, duree_heures, prix_fcfa, ecrans_max, qualite_max, etiquette, description, actif) VALUES
    (nextval('pass_id_seq'), 'PASS_JOUR',    'Pass Jour',    24,  200,  1, 'HD_1080P', 'Pour essayer',   'Accès illimité pendant 24 h, 1 écran.', TRUE),
    (nextval('pass_id_seq'), 'PASS_SEMAINE', 'Pass Semaine', 168, 1000, 1, 'HD_1080P', 'Le plus choisi', '7 jours d''accès illimité, 1 écran.',   TRUE),
    (nextval('pass_id_seq'), 'PASS_MOIS',    'Pass Mois',    720, 3000, 2, 'HD_1080P', 'Meilleur prix',  '30 jours, 2 écrans, qualité HD.',      TRUE);
