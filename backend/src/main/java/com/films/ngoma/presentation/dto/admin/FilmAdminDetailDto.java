package com.films.ngoma.presentation.dto.admin;

import com.films.ngoma.domain.Film;
import com.films.ngoma.domain.Genre;
import com.films.ngoma.domain.Pays;
import com.films.ngoma.enums.StatutFilm;
import com.films.ngoma.enums.TypeContenu;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Fiche complète d'un film pour le formulaire du back-office.
 * {@code elementsManquants} liste ce qui empêche la publication (vide si le film est complet).
 */
public record FilmAdminDetailDto(Long id, String slug, String titre, TypeContenu type, String synopsisCourt,
								 String synopsis, Integer annee, Integer dureeMinutes, String classificationAge,
								 LocalDate dateSortie, List<Long> genreIds, List<String> paysCodes,
								 boolean gratuit, boolean exclusivite, boolean telechargeable,
								 String afficheUrl, String bandeauUrl, String bandeAnnonceUrl,
								 List<ParticipationDto> distribution, List<FichierVideoDto> videos,
								 List<SousTitreDto> sousTitres, StatutFilm statut, List<String> elementsManquants,
								 long version, LocalDateTime creeLe, LocalDateTime modifieLe) {

	public FilmAdminDetailDto(Film f) {
		this(f.getId(), f.getSlug(), f.getTitre(), f.getType(), f.getSynopsisCourt(), f.getSynopsis(),
				f.getAnnee(), f.getDureeMinutes(), f.getClassificationAge(), f.getDateSortie(),
				f.getGenres().stream().map(Genre::getId).sorted().toList(),
				f.getPays().stream().map(Pays::getCode).sorted().toList(),
				f.isGratuit(), f.isExclusivite(), f.isTelechargeable(),
				f.getAfficheUrl(), f.getBandeauUrl(), f.getBandeAnnonceUrl(),
				f.getDistribution().stream().map(ParticipationDto::new).toList(),
				f.getVideos().stream().map(FichierVideoDto::new).toList(),
				f.getSousTitres().stream().map(SousTitreDto::new).toList(),
				f.getStatut(), f.elementsManquantsPourPublication(),
				f.getVersion(), f.getCreateAt(), f.getUpdateAt());
	}
}
