package com.films.ngoma.exception;

import com.films.ngoma.exception.configuration.AbstractApplicationException;
import com.films.ngoma.exception.configuration.EntiteNonConnueException;

import java.util.Collection;
import java.util.List;

import static java.util.Collections.singleton;

/**
 * Erreurs métier liées aux films (HTTP 400 via le gestionnaire des AbstractApplicationException).
 */
public class FilmException extends AbstractApplicationException {

	private FilmException(CodeErreur code, String message, Collection<String> parametres) {
		super(code, message, parametres);
	}

	public static EntiteNonConnueException filmInconnu(String slug) {
		return new EntiteNonConnueException("Aucun film trouvé pour « %s »", singleton(slug));
	}

	public static EntiteNonConnueException filmIntrouvable(Long id) {
		return new EntiteNonConnueException("Aucun film trouvé avec l'identifiant %s", singleton(String.valueOf(id)));
	}

	public static FilmException imageInvalide() {
		return new FilmException(CodeErreur.IMAGE_INVALIDE,
				"L'image doit être un fichier JPEG, PNG ou WebP de 5 Mo maximum.", null);
	}

	public static FilmException videoInvalide() {
		return new FilmException(CodeErreur.VIDEO_INVALIDE,
				"Le fichier doit être une vidéo MP4.", null);
	}

	public static FilmException videoTropVolumineuse(String tailleMax) {
		return new FilmException(CodeErreur.VIDEO_TROP_VOLUMINEUSE,
				"La vidéo dépasse la taille maximale autorisée (%s).", singleton(tailleMax));
	}

	public static FilmException sousTitreInvalide() {
		return new FilmException(CodeErreur.SOUS_TITRE_INVALIDE,
				"Le fichier de sous-titres doit être au format .vtt ou .srt (1 Mo maximum), avec une langue et un libellé.", null);
	}

	public static FilmException referentielInconnu() {
		return new FilmException(CodeErreur.REFERENTIEL_INCONNU,
				"Un des genres ou des pays sélectionnés n'existe pas.", null);
	}

	/**
	 * Publication refusée : éléments obligatoires manquants (ex. « l'affiche, le synopsis »).
	 */
	public static FilmException filmIncomplet(List<String> manquants) {
		return new FilmException(CodeErreur.FILM_INCOMPLET,
				"Le film ne peut pas être publié : il manque %s.", singleton(String.join(", ", manquants)));
	}
}
