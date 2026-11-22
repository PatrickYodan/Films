package com.films.ngoma.service;

import java.io.InputStream;

/**
 * Stockage des fichiers publics (affiches, bandeaux…).
 * Implémentation locale pour le développement ; une implémentation cloud (S3, R2, Bunny…) viendra plus tard.
 */
public interface StockageService {

	/**
	 * Enregistre un fichier.
	 *
	 * @param dossier  sous-dossier logique, ex. « films/lagos-express »
	 * @param nom      nom du fichier, ex. « affiche-3f2a9c.jpg »
	 * @param octets   contenu
	 * @param typeMime type MIME du contenu
	 * @return l'URL publique (absolue) du fichier
	 */
	String enregistrer(String dossier, String nom, byte[] octets, String typeMime);

	/**
	 * Supprime un fichier à partir de son URL publique. Sans effet si l'URL n'est pas gérée par ce stockage
	 * (ex. images de démonstration servies par le frontend).
	 */
	void supprimer(String urlPublique);

	/**
	 * Enregistre un gros fichier (vidéo) en le copiant par blocs, sans le charger en mémoire.
	 *
	 * @param tailleMax taille maximale acceptée, en octets
	 * @return l'URL publique et la taille réelle du fichier
	 * @throws TailleMaximaleDepassee si le flux dépasse {@code tailleMax} (le fichier partiel est supprimé)
	 */
	FichierStocke enregistrerFlux(String dossier, String nom, InputStream flux, long tailleMax, String typeMime);

	record FichierStocke(String url, long taille) {
	}

	/** Le fichier envoyé dépasse la taille autorisée. */
	final class TailleMaximaleDepassee extends RuntimeException {
		public TailleMaximaleDepassee() {
			super("Taille maximale dépassée");
		}
	}
}
