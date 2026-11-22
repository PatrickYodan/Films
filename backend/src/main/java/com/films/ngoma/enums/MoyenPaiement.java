package com.films.ngoma.enums;

/**
 * Moyen de paiement.
 */
public enum MoyenPaiement {
	ORANGE_MONEY("Orange Money"),
	MTN_MOMO("MTN MoMo"),
	WAVE("Wave"),
	MOOV_MONEY("Moov Money"),
	CARTE("Carte bancaire");

	private final String libelle;

	MoyenPaiement(String libelle) {
		this.libelle = libelle;
	}

	public String getLibelle() {
		return libelle;
	}
}
