package com.films.ngoma.exception.configuration;

import java.util.Collection;

/**
 * Classe parente de toutes les exceptions liées à un code erreur.
 * Chaque code erreur doit être utilisé par une et une seule exception.
 */
public abstract class AbstractApplicationException extends RuntimeException {

	private final Long codeErreur;
	private final String typeErreur;
	private final Collection<String> parametres;

	protected AbstractApplicationException(AbstractCodeErreur codeErreur, String message) {
		this(codeErreur, message, null);
	}

	protected AbstractApplicationException(AbstractCodeErreur codeErreur, String message, Collection<String> parametres) {
		super(message);
		this.codeErreur = codeErreur.getCode();
		this.typeErreur = TypeErreur.ERROR.name();
		this.parametres = parametres;
	}

	public ApplicationErreur getApplicationErreur() {
		return new ApplicationErreur(codeErreur, typeErreur, super.getMessage(), parametres);
	}

	public final String getMessageAvecCode() {
		return getApplicationErreur().getMessageAvecCode();
	}

	@Override
	public String getMessage() {
		return getApplicationErreur().getMessage();
	}
}
