package com.films.ngoma.domain;

import com.films.ngoma.enums.Fonction;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.io.Serializable;
import java.util.Objects;

/**
 * Clé composite de {@link Participation} : (film, personne, fonction).
 */
@Embeddable
public class ParticipationId implements Serializable {

	@Column(name = "film_id")
	private Long filmId;

	@Column(name = "personne_id")
	private Long personneId;

	@Enumerated(EnumType.STRING)
	private Fonction fonction;

	protected ParticipationId() {
	}

	public ParticipationId(Long filmId, Long personneId, Fonction fonction) {
		this.filmId = filmId;
		this.personneId = personneId;
		this.fonction = fonction;
	}

	public Long getFilmId() {
		return filmId;
	}

	public Long getPersonneId() {
		return personneId;
	}

	public Fonction getFonction() {
		return fonction;
	}

	@Override
	public boolean equals(Object o) {
		return this == o || (o instanceof ParticipationId autre
				&& Objects.equals(filmId, autre.filmId)
				&& Objects.equals(personneId, autre.personneId)
				&& fonction == autre.fonction);
	}

	@Override
	public int hashCode() {
		return Objects.hash(filmId, personneId, fonction);
	}
}
