package com.films.ngoma.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Photo de profil d'un utilisateur (une au plus).
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = UtilisateurPhoto.TABLE_NAME)
public class UtilisateurPhoto {

	public static final String TABLE_NAME = "utilisateur_photo";

	@Id
	@Column(name = "utilisateur_id")
	private Long utilisateurId;

	private byte[] contenu;

	@Column(name = "type_mime")
	private String typeMime;

	@Column(name = "maj_le")
	private LocalDateTime majLe;

	protected UtilisateurPhoto() {
	}

	public UtilisateurPhoto(Long utilisateurId) {
		this.utilisateurId = utilisateurId;
	}

	public void remplacer(byte[] contenu, String typeMime) {
		this.contenu = contenu;
		this.typeMime = typeMime;
		this.majLe = LocalDateTime.now();
	}

	public Long getUtilisateurId() {
		return utilisateurId;
	}

	public byte[] getContenu() {
		return contenu;
	}

	public String getTypeMime() {
		return typeMime;
	}

	public LocalDateTime getMajLe() {
		return majLe;
	}
}
