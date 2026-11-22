package com.films.ngoma.domain;

import com.films.ngoma.enums.JpaConstants;
import jakarta.persistence.*;

/**
 * Piste de sous-titres WebVTT d'un film (une par langue).
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = SousTitre.TABLE_NAME)
public class SousTitre implements JpaConstants {

	public static final String TABLE_NAME = "sous_titre";
	public static final String TABLE_ID = TABLE_NAME + ID;
	public static final String TABLE_SEQ = TABLE_ID + SEQ;

	@Id
	@SequenceGenerator(name = TABLE_SEQ, sequenceName = TABLE_SEQ, allocationSize = 50)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABLE_SEQ)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "film_id")
	private Film film;

	private String langue;
	private String libelle;

	@Column(name = "fichier_vtt_url")
	private String fichierVttUrl;

	protected SousTitre() {
	}

	SousTitre(Film film, String langue, String libelle, String fichierVttUrl) {
		this.film = film;
		this.langue = langue;
		modifier(libelle, fichierVttUrl);
	}

	void modifier(String libelle, String fichierVttUrl) {
		this.libelle = libelle;
		this.fichierVttUrl = fichierVttUrl;
	}

	public Long getId() {
		return id;
	}

	public Film getFilm() {
		return film;
	}

	public String getLangue() {
		return langue;
	}

	public String getLibelle() {
		return libelle;
	}

	public String getFichierVttUrl() {
		return fichierVttUrl;
	}
}
