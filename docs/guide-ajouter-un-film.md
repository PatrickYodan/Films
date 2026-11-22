# Ajouter un film sur NGOMA

Guide pour l'équipe contenu. Durée : environ 10 minutes par film, une fois les fichiers prêts.

## Avant de commencer

Rassemblez :

| Élément | Format | Obligatoire pour publier |
|---|---|---|
| Titre, année, durée, genres, pays | — | Titre, au moins un genre et un pays |
| Synopsis | Quelques phrases | Oui |
| Affiche | Image **verticale** (format 2:3, ex. 800 × 1200), JPEG, PNG ou WebP, 5 Mo max | Oui |
| Bandeau | Image **horizontale** (format 16:9, ex. 1920 × 1080), JPEG, PNG ou WebP, 5 Mo max | Oui |
| Vidéo | Fichier **MP4** (4 Go maximum), ou lien `.m3u8` / `.mp4` fourni par l'équipe technique | Au moins une qualité |
| Distribution, sous-titres, bande-annonce | — | Non |

Deux façons d'ajouter une vidéo :

- **Envoyer le fichier MP4** directement depuis le back-office (le plus simple). Un même fichier peut servir pour
  plusieurs qualités ; idéalement, une version par qualité (240p légère pour la 3G, 480p, 1080p).
- **Coller un lien** fourni par l'équipe technique : manifeste HLS `.m3u8` (recommandé : une seule vidéo contient toutes
  les résolutions et s'adapte à la connexion), ou adresse d'un fichier `.mp4`.

## 1. Ouvrir le back-office

1. Connectez-vous avec votre compte administrateur.
2. Cliquez sur **Administration** dans le bandeau du haut (le bouton n'apparaît que pour les administrateurs).
3. Cliquez sur **+ Ajouter un film**.

## 2. Remplir les informations

- **Titre** : tel qu'il doit apparaître sur le site. L'adresse de la page du film (`/film/…`) est créée
  automatiquement à partir du titre lors du premier enregistrement, et **ne change plus ensuite**, même si vous
  corrigez le titre : les liens déjà partagés continuent de fonctionner.
- **Synopsis court** : une phrase d'accroche. **Synopsis** : le résumé complet (obligatoire pour publier).
- **Genres** et **Pays** : cochez au moins un de chaque.
- **Gratuit** : le film est visible sans pass. **Exclusivité** : affiche le badge « Exclusivité ».

Cliquez sur **Enregistrer le brouillon**. Le film existe maintenant, mais il est **invisible pour le public**.

## 3. Ajouter les images

Après le premier enregistrement, la section **Images** devient active.

1. **Affiche** → *Envoyer une image* → choisissez le fichier. Une barre de progression s'affiche.
2. **Bandeau** → même chose.

Les images sont enregistrées dès l'envoi (pas besoin de cliquer sur « Enregistrer »). Pour changer une image,
cliquez sur *Remplacer*.

## 4. Distribution, vidéos, sous-titres

- **Distribution** : *+ Ajouter une personne*, puis nom, fonction (acteur, réalisation…) et rôle. L'ordre des
  lignes est l'ordre d'affichage. Une personne déjà connue (même nom) est réutilisée automatiquement.
- **Vidéos** : pour chaque qualité (240p, 480p, 1080p), cliquez sur **Envoyer un fichier MP4** ou glissez le fichier
  sur la zone de la qualité. Une barre de progression s'affiche (bouton *Annuler* pour interrompre) ; à la fin, le lien se
  remplit tout seul et la taille du fichier s'affiche. Vous pouvez aussi coller un lien `.m3u8` ou `.mp4`. Au moins une
  qualité est nécessaire pour publier. L'envoi d'un gros film peut prendre plusieurs minutes : gardez la page ouverte.
- **Sous-titres** : une ligne par langue. Renseignez le code de langue (`fr`, `en`, `wo`…) et le libellé affiché
  aux spectateurs (« Français »), puis cliquez sur **Envoyer un .vtt ou .srt**. Les fichiers `.srt` sont convertis
  automatiquement, accents compris. Vous pouvez aussi coller le lien d'un fichier `.vtt`. Les spectateurs choisissent
  ensuite la langue sous la vidéo (ou les désactivent) ; leur choix est mémorisé.

Cliquez sur **Enregistrer le brouillon**.

## 5. Publier

Cliquez sur **Publier**. Si quelque chose manque, un message l'indique, par exemple
« Le film ne peut pas être publié : il manque l'affiche, le bandeau. » — complétez puis réessayez.
La ligne « Pour publier, il manque : … » au-dessus des boutons vous le signale à l'avance.

Une fois publié, le film apparaît dans le catalogue, l'accueil (Nouveautés…) et la recherche.
Le bouton **Voir dans le catalogue** ouvre sa page publique.

## Modifier, retirer ou supprimer un film

Depuis **Administration → Films** :

- **Modifier** : ouvre le formulaire. Sur un film publié, les modifications enregistrées sont **visibles
  immédiatement** par le public.
- **Dépublier** : retire le film du catalogue sans rien perdre ; il redevient un brouillon.
- **Supprimer** : efface définitivement le film, ses images, sa distribution et l'historique de visionnage.
  Préférez *Dépublier* en cas de doute.

La recherche et le filtre « Brouillon / Publié » aident à retrouver un film.

## En cas de message d'erreur

| Message | Que faire |
|---|---|
| « Ce film a été modifié entre-temps… » | Quelqu'un (ou vous, dans un autre onglet) a enregistré ce film. Cliquez sur **Recharger le film**, puis refaites vos modifications. |
| « L'image doit être un fichier JPEG, PNG ou WebP de 5 Mo maximum. » | Réexportez l'image dans un de ces formats ou réduisez sa taille. |
| « Lien invalide : un fichier .m3u8 ou .mp4… » | Vérifiez le lien (il doit finir par `.m3u8` ou `.mp4`). |
| « Choisissez un fichier vidéo MP4 de 4 Go maximum. » / « Le fichier doit être une vidéo MP4. » | Exportez la vidéo en MP4 (H.264), par exemple avec HandBrake, en réduisant la taille si besoin. |
| « Veuillez vous connecter » / retour à la page de connexion | Votre session a expiré : reconnectez-vous. Les modifications non enregistrées sont perdues. |
| « Accès interdit » | Votre compte n'est pas administrateur : demandez l'accès à un responsable. |
