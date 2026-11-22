package com.films.ngoma.exception.configuration;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Objet transportant une erreur de l'application (code, type, message, paramètres).
 */
public class ApplicationErreur {

	private final Long code;
	private final String type;
	private final String message;
	private final Collection<String> parametres = new ArrayList<>();

	public ApplicationErreur(AbstractCodeErreur codeErreur, String message) {
		this(codeErreur.getCode(), TypeErreur.ERROR.name(), message);
	}

	public ApplicationErreur(Long code, String type, String message) {
		this.code = code;
		this.type = type;
		this.message = message;
	}

	public ApplicationErreur(Long code, String type, String message, Collection<String> parametres) {
		this(code, type, message);
		if (parametres != null) {
			this.parametres.addAll(parametres);
		}
	}

	public Long getCode() {
		return code;
	}

	public String getType() {
		return type;
	}

	/** @return le message avec ses paramètres substitués. */
	public String getMessage() {
		return parametres.isEmpty() ? message : String.format(message, parametres.toArray());
	}

	public Collection<String> getParametres() {
		return parametres;
	}

	/** @return le message préfixé par le code de l'erreur. */
	public String getMessageAvecCode() {
		return MessageFormat.format("[{0,number,0000}] {1}", code, getMessage());
	}
}
