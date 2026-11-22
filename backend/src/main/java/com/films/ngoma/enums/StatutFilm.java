package com.films.ngoma.enums;

/**
 * Statut éditorial d'un film. Seuls les films PUBLIE sont visibles dans le catalogue public.
 */
public enum StatutFilm {
	BROUILLON("Brouillon"),
	PUBLIE("Publié");

	private final String libelle;

	StatutFilm(String libelle) {
		this.libelle = libelle;
	}

	public String getLibelle() {
		return libelle;
	}
}
