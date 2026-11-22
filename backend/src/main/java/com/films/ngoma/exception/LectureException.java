package com.films.ngoma.exception;

import com.films.ngoma.exception.configuration.AbstractApplicationException;
import org.springframework.http.HttpStatus;

/**
 * Erreurs d'ouverture de la lecture d'un film.
 */
public class LectureException extends AbstractApplicationException {

	private final HttpStatus statut;

	private LectureException(CodeErreur code, HttpStatus statut, String message) {
		super(code, message, null);
		this.statut = statut;
	}

	public HttpStatus getStatut() {
		return statut;
	}

	/** Film payant sans pass actif. */
	public static LectureException passNecessaire() {
		return new LectureException(CodeErreur.PASS_NECESSAIRE, HttpStatus.FORBIDDEN,
				"Un pass actif est nécessaire pour regarder ce film.");
	}

	/** Aucune vidéo n'est encore associée au film. */
	public static LectureException videoIndisponible() {
		return new LectureException(CodeErreur.VIDEO_INDISPONIBLE, HttpStatus.NOT_FOUND,
				"La vidéo de ce film n'est pas encore disponible.");
	}
}
