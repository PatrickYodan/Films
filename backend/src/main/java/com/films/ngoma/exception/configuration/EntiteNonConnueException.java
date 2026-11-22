package com.films.ngoma.exception.configuration;

import java.util.Collection;

/**
 * Exception levée lorsqu'une entité n'a pas été trouvée en base.
 */
public class EntiteNonConnueException extends AbstractApplicationException {

	public EntiteNonConnueException(String message, Collection<String> parametres) {
		super(CodeErreurTechnique.AUCUN_RESULTAT, message, parametres);
	}
}
