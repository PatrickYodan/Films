package com.films.ngoma.enums;

/**
 * Statut d'un paiement.
 */
public enum StatutPaiement {
	EN_ATTENTE("En attente de confirmation"),
	CONFIRME("Confirmé"),
	ECHOUE("Échoué"),
	EXPIRE("Expiré");

	private final String libelle;

	StatutPaiement(String libelle) {
		this.libelle = libelle;
	}

	public String getLibelle() {
		return libelle;
	}
}
