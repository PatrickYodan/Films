package com.films.ngoma.domain;

import jakarta.persistence.*;

/**
 * Pays d'origine d'un film (référentiel, identifié par son code ISO 3166-1 alpha-2).
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = Pays.TABLE_NAME)
public class Pays {

	public static final String TABLE_NAME = "pays";

	@Id
	@Column(length = 2)
	private String code;

	private String nom;

	@Column(name = "drapeau_url")
	private String drapeauUrl;

	protected Pays() {
	}

	public String getCode() {
		return code;
	}

	public String getNom() {
		return nom;
	}

	public String getDrapeauUrl() {
		return drapeauUrl;
	}
}
