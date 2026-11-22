package com.films.ngoma.enums;

/**
 * Type de contenu du catalogue.
 */
public enum TypeContenu {
	FILM("Film"),
	SERIE("Série");

	private final String libelle;

	TypeContenu(String libelle) {
		this.libelle = libelle;
	}

	public String getLibelle() {
		return libelle;
	}
}
