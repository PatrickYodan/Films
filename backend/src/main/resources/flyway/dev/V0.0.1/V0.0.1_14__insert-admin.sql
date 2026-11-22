-- Compte administrateur de développement.
-- Le mot de passe n'est stocké que sous forme de hachage BCrypt (jamais en clair).
INSERT INTO utilisateur (id, nom, email, telephone, mot_de_passe_hash, role, create_by, create_at)
VALUES (nextval('utilisateur_id_seq'), 'Patrick Yodan', 'patrickyodan16@gmail.com', NULL,
        '$2a$10$mWhUrM/9QtUduzXdKpvbK.NBLZe.6FpCeU8FP5QrZXhouyazAYWt2', 'ADMIN', 'FLYWAY', now())
ON CONFLICT (email) DO UPDATE SET role = 'ADMIN', mot_de_passe_hash = EXCLUDED.mot_de_passe_hash;
