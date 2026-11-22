package com.films.ngoma.enums;

/**
 * Fonction d'une personne dans un film.
 */
public enum Fonction {
	ACTEUR("Acteur"),
	REALISATEUR("Réalisateur"),
	SCENARISTE("Scénariste"),
	PRODUCTEUR("Producteur");

	private final String libelle;

	Fonction(String libelle) {
		this.libelle = libelle;
	}

	public String getLibelle() {
		return libelle;
	}
}
