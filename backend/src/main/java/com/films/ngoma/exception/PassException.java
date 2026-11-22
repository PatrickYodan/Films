package com.films.ngoma.exception;

import com.films.ngoma.exception.configuration.AbstractApplicationException;

import java.util.Collection;

import static java.util.Collections.singleton;

public class PassException extends AbstractApplicationException {

	private PassException(CodeErreur code, String message, Collection<String> parametres) {
		super(code, message, parametres);
	}

	public static PassException passInconnu(String code) {
		return new PassException(CodeErreur.PASS_INCONNU, "Le pass %s n'existe pas ou n'est plus disponible", singleton(code));
	}
}
