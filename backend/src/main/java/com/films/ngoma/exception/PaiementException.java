package com.films.ngoma.exception;

import com.films.ngoma.exception.configuration.AbstractApplicationException;

import java.util.Collection;

import static java.util.Collections.singleton;

public class PaiementException extends AbstractApplicationException {

	private PaiementException(CodeErreur code, String message, Collection<String> parametres) {
		super(code, message, parametres);
	}

	public static PaiementException paiementInconnu(String reference) {
		return new PaiementException(CodeErreur.PAIEMENT_INCONNU, "Aucun paiement trouvé avec la référence %s", singleton(reference));
	}

	public static PaiementException dejaTraite(String reference) {
		return new PaiementException(CodeErreur.PAIEMENT_DEJA_TRAITE, "Le paiement %s a déjà été traité", singleton(reference));
	}

	public static PaiementException signatureInvalide() {
		return new PaiementException(CodeErreur.SIGNATURE_INVALIDE, "Signature de la notification de paiement invalide", null);
	}

	public static PaiementException telephoneObligatoire() {
		return new PaiementException(CodeErreur.TELEPHONE_OBLIGATOIRE, "Le numéro de téléphone est obligatoire pour un paiement Mobile Money", null);
	}
}
