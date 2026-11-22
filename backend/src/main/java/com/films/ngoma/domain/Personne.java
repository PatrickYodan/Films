package com.films.ngoma.domain;

import com.films.ngoma.enums.JpaConstants;
import jakarta.persistence.*;

/**
 * Personne de la distribution (acteur, réalisateur…), partagée entre les films.
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = Personne.TABLE_NAME)
public class Personne implements JpaConstants {

	public static final String TABLE_NAME = "personne";
	public static final String TABLE_ID = TABLE_NAME + ID;
	public static final String TABLE_SEQ = TABLE_ID + SEQ;

	@Id
	@SequenceGenerator(name = TABLE_SEQ, sequenceName = TABLE_SEQ, allocationSize = 50)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABLE_SEQ)
	private Long id;

	private String nom;

	@Column(name = "photo_url")
	private String photoUrl;

	protected Personne() {
	}

	public Personne(String nom) {
		this.nom = nom.trim().replaceAll("\\s+", " ");
	}

	public Long getId() {
		return id;
	}

	public String getNom() {
		return nom;
	}

	public String getPhotoUrl() {
		return photoUrl;
	}
}
