package com.films.ngoma.domain;

import com.films.ngoma.enums.JpaConstants;
import com.films.ngoma.enums.Qualite;
import jakarta.persistence.*;

/**
 * Rendition HLS d'un film pour une qualité donnée (une au plus par qualité).
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = FichierVideo.TABLE_NAME)
public class FichierVideo implements JpaConstants {

	public static final String TABLE_NAME = "fichier_video";
	public static final String TABLE_ID = TABLE_NAME + ID;
	public static final String TABLE_SEQ = TABLE_ID + SEQ;

	@Id
	@SequenceGenerator(name = TABLE_SEQ, sequenceName = TABLE_SEQ, allocationSize = 50)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABLE_SEQ)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "film_id")
	private Film film;

	@Enumerated(EnumType.STRING)
	private Qualite qualite;

	@Column(name = "chemin_hls")
	private String cheminHls;

	@Column(name = "taille_octets")
	private Long tailleOctets;

	protected FichierVideo() {
	}

	FichierVideo(Film film, Qualite qualite, String cheminHls, Long tailleOctets) {
		this.film = film;
		this.qualite = qualite;
		modifier(cheminHls, tailleOctets);
	}

	void modifier(String cheminHls, Long tailleOctets) {
		this.cheminHls = cheminHls;
		this.tailleOctets = tailleOctets;
	}

	public Long getId() {
		return id;
	}

	public Film getFilm() {
		return film;
	}

	public Qualite getQualite() {
		return qualite;
	}

	public String getCheminHls() {
		return cheminHls;
	}

	public Long getTailleOctets() {
		return tailleOctets;
	}
}
