package com.films.ngoma.domain;

import com.films.ngoma.enums.Fonction;
import jakarta.persistence.*;

/**
 * Participation d'une personne à un film, dans une fonction donnée (composition de {@link Film}).
 */
@Entity
@Access(AccessType.FIELD)
@Table(name = Participation.TABLE_NAME)
public class Participation {

	public static final String TABLE_NAME = "participation";

	@EmbeddedId
	private ParticipationId id;

	@MapsId("filmId")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "film_id")
	private Film film;

	@MapsId("personneId")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "personne_id")
	private Personne personne;

	private String role;
	private int ordre;

	protected Participation() {
	}

	Participation(Film film, Personne personne, Fonction fonction, String role, int ordre) {
		this.id = new ParticipationId(film.getId(), personne.getId(), fonction);
		this.film = film;
		this.personne = personne;
		this.role = role;
		this.ordre = ordre;
	}

	void modifier(String role, int ordre) {
		this.role = role;
		this.ordre = ordre;
	}

	/** Clé fonctionnelle (personne + fonction), indépendante de l'identifiant du film. */
	String cle() {
		return cle(personne, getFonction());
	}

	static String cle(Personne personne, Fonction fonction) {
		return personne.getId() + ":" + fonction;
	}

	public ParticipationId getId() {
		return id;
	}

	public Film getFilm() {
		return film;
	}

	public Personne getPersonne() {
		return personne;
	}

	public Fonction getFonction() {
		return id.getFonction();
	}

	public String getRole() {
		return role;
	}

	public int getOrdre() {
		return ordre;
	}
}
