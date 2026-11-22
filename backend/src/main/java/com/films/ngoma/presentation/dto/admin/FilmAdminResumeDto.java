package com.films.ngoma.presentation.dto.admin;

import com.films.ngoma.domain.Film;
import com.films.ngoma.domain.Genre;
import com.films.ngoma.domain.Pays;
import com.films.ngoma.enums.StatutFilm;
import com.films.ngoma.enums.TypeContenu;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Ligne du tableau des films du back-office.
 */
public record FilmAdminResumeDto(Long id, String slug, String titre, TypeContenu type, Integer annee,
								 String afficheUrl, List<String> pays, List<String> genres, StatutFilm statut,
								 LocalDateTime modifieLe) {

	public FilmAdminResumeDto(Film f) {
		this(f.getId(), f.getSlug(), f.getTitre(), f.getType(), f.getAnnee(), f.getAfficheUrl(),
				f.getPays().stream().map(Pays::getNom).toList(),
				f.getGenres().stream().map(Genre::getNom).toList(),
				f.getStatut(),
				f.getUpdateAt() != null ? f.getUpdateAt() : f.getCreateAt());
	}
}
