package com.films.ngoma.presentation.dto.film;

import com.films.ngoma.domain.Film;
import com.films.ngoma.domain.Genre;
import com.films.ngoma.domain.Pays;

import java.util.List;

/**
 * Carte film (rails de l'accueil, catalogue, Ma liste).
 */
public record FilmResumeDto(Long id, String slug, String titre, String synopsisCourt, Integer annee, Integer dureeMinutes,
							String classificationAge, String afficheUrl, String bandeauUrl, boolean gratuit, boolean exclusivite,
							List<String> genres, List<String> pays) {

	public FilmResumeDto(Film film) {
		this(film.getId(), film.getSlug(), film.getTitre(), film.getSynopsisCourt(), film.getAnnee(), film.getDureeMinutes(),
				film.getClassificationAge(), film.getAfficheUrl(), film.getBandeauUrl(), film.isGratuit(), film.isExclusivite(),
				film.getGenres().stream().map(Genre::getNom).toList(),
				film.getPays().stream().map(Pays::getCode).toList());
	}
}
