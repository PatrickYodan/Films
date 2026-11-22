package com.films.ngoma.enums;

/**
 * Rôle d'un utilisateur.
 */
public enum Role {
	ABONNE("Abonné"),
	ADMIN("Administrateur");

	private final String libelle;

	Role(String libelle) {
		this.libelle = libelle;
	}

	public String getLibelle() {
		return libelle;
	}
}
