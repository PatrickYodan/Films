package com.films.ngoma.exception.configuration;

/**
 * Interface des codes erreurs de l'application.
 */
public interface AbstractCodeErreur {

	/** @return le code de l'erreur. */
	Long getCode();

	/** @return le type de l'erreur. */
	String getType();
}
