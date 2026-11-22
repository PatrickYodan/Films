package com.films.ngoma.exception;

import com.films.ngoma.exception.configuration.AbstractApplicationException;
import com.films.ngoma.exception.configuration.AbstractCodeErreur;
import com.films.ngoma.exception.configuration.CodeErreurTechnique;
import org.springframework.http.HttpStatus;

import java.util.Collection;

import static java.util.Collections.singleton;

public class UtilisateurException extends AbstractApplicationException {

	private final HttpStatus statut;

	private UtilisateurException(AbstractCodeErreur code, HttpStatus statut, String message, Collection<String> parametres) {
		super(code, message, parametres);
		this.statut = statut;
	}

	public HttpStatus getStatut() {
		return statut;
	}

	/** Identifiants de connexion incorrects (on ne précise pas lequel). */
	public static UtilisateurException identifiantsIncorrects() {
		return new UtilisateurException(CodeErreurTechnique.MOT_DE_PASSE_INCORRECT, HttpStatus.UNAUTHORIZED,
				"Adresse e-mail ou mot de passe incorrect", null);
	}

	public static UtilisateurException utilisateurInconnu(String email) {
		return new UtilisateurException(CodeErreurTechnique.UTILISATEUR_INCONNU, HttpStatus.NOT_FOUND,
				"Aucun utilisateur trouvé avec l'adresse %s", singleton(email));
	}

	public static UtilisateurException emailDejaUtilise(String email) {
		return new UtilisateurException(CodeErreur.EMAIL_DEJA_UTILISE, HttpStatus.CONFLICT,
				"Un compte existe déjà avec l'adresse %s", singleton(email));
	}

	public static UtilisateurException sessionExpiree() {
		return new UtilisateurException(CodeErreurTechnique.ACCES_REFUSE, HttpStatus.UNAUTHORIZED,
				"Votre session a expiré, veuillez vous reconnecter", null);
	}

	public static UtilisateurException photoInvalide() {
		return new UtilisateurException(CodeErreur.PHOTO_INVALIDE, HttpStatus.BAD_REQUEST,
				"La photo doit être une image JPEG, PNG ou WebP de 2 Mo maximum", null);
	}

	public static UtilisateurException aucunePhoto() {
		return new UtilisateurException(CodeErreurTechnique.AUCUN_RESULTAT, HttpStatus.NOT_FOUND,
				"Aucune photo de profil", null);
	}

	public static UtilisateurException motDePasseActuelIncorrect() {
		return new UtilisateurException(CodeErreur.MOT_DE_PASSE_ACTUEL_INCORRECT, HttpStatus.BAD_REQUEST,
				"Le mot de passe actuel est incorrect", null);
	}
}
