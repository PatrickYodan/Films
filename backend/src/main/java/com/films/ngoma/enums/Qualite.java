package com.films.ngoma.enums;

/**
 * Qualité de lecture vidéo.
 */
public enum Qualite {
	ECO_240P("Économie · 240p"),
	STANDARD_480P("Standard · 480p"),
	HD_1080P("HD · 1080p");

	private final String libelle;

	Qualite(String libelle) {
		this.libelle = libelle;
	}

	public String getLibelle() {
		return libelle;
	}
}
