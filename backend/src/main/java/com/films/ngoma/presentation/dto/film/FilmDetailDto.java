package com.films.ngoma.presentation.dto.film;

import com.films.ngoma.domain.Film;
import com.films.ngoma.enums.TypeContenu;
import com.films.ngoma.presentation.dto.GenreDto;
import com.films.ngoma.presentation.dto.PaysDto;

import java.time.LocalDate;
import java.util.List;

/**
 * Fiche film complète.
 */
public record FilmDetailDto(Long id, String slug, String titre, String synopsisCourt, String synopsis,
							Integer annee, Integer dureeMinutes, String classificationAge, TypeContenu type,
							boolean gratuit, boolean exclusivite, boolean telechargeable,
							String afficheUrl, String bandeauUrl, String bandeAnnonceUrl, LocalDate dateSortie,
							List<GenreDto> genres, List<PaysDto> pays) {

	public FilmDetailDto(Film f) {
		this(f.getId(), f.getSlug(), f.getTitre(), f.getSynopsisCourt(), f.getSynopsis(),
				f.getAnnee(), f.getDureeMinutes(), f.getClassificationAge(), f.getType(),
				f.isGratuit(), f.isExclusivite(), f.isTelechargeable(),
				f.getAfficheUrl(), f.getBandeauUrl(), f.getBandeAnnonceUrl(), f.getDateSortie(),
				f.getGenres().stream().map(GenreDto::new).toList(),
				f.getPays().stream().map(PaysDto::new).toList());
	}
}
