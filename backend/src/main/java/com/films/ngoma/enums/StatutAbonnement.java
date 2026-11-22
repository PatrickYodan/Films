package com.films.ngoma.enums;

/**
 * Statut d'un abonnement.
 */
public enum StatutAbonnement {
	ACTIF("Actif"),
	EXPIRE("Expiré"),
	ANNULE("Annulé");

	private final String libelle;

	StatutAbonnement(String libelle) {
		this.libelle = libelle;
	}

	public String getLibelle() {
		return libelle;
	}
}
