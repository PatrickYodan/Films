package com.films.ngoma.exception;

import com.films.ngoma.exception.configuration.AbstractCodeErreur;

/**
 * Codes erreurs métier de NGOMA (7000 - 7999).
 */
public enum CodeErreur implements AbstractCodeErreur {
	// Utilisateur 70xx
	EMAIL_DEJA_UTILISE(7001L),
	PHOTO_INVALIDE(7002L),
	MOT_DE_PASSE_ACTUEL_INCORRECT(7003L),
	// Film 71xx
	FILM_INCONNU(7101L),
	FILM_INCOMPLET(7102L),
	IMAGE_INVALIDE(7103L),
	REFERENTIEL_INCONNU(7104L),
	VIDEO_INVALIDE(7105L),
	VIDEO_TROP_VOLUMINEUSE(7106L),
	SOUS_TITRE_INVALIDE(7107L),
	// Pass / abonnement 72xx
	PASS_INCONNU(7201L),
	AUCUN_ABONNEMENT_ACTIF(7202L),
	// Paiement 73xx
	PAIEMENT_INCONNU(7301L),
	PAIEMENT_DEJA_TRAITE(7302L),
	SIGNATURE_INVALIDE(7303L),
	TELEPHONE_OBLIGATOIRE(7304L),
	// Lecture 74xx
	PASS_NECESSAIRE(7401L),
	VIDEO_INDISPONIBLE(7402L);

	public static final int CODE_MIN = 7000;
	public static final int CODE_MAX = 7999;

	private final Long code;

	CodeErreur(Long code) {
		if (code < CODE_MIN || code > CODE_MAX) {
			throw new IllegalStateException(String.format("Le code erreur doit se trouver entre %s et %s", CODE_MIN, CODE_MAX));
		}
		this.code = code;
	}

	@Override
	public Long getCode() {
		return code;
	}

	@Override
	public String getType() {
		return name();
	}
}
