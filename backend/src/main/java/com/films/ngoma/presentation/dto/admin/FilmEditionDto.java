package com.films.ngoma.presentation.dto.admin;

import com.films.ngoma.domain.InformationsFilm;
import com.films.ngoma.enums.TypeContenu;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

/**
 * Données saisies pour créer ou modifier un film.
 * {@code version} est obligatoire en modification (verrou optimiste) et ignorée à la création.
 */
public record FilmEditionDto(
		@NotBlank @Size(max = 255) String titre,
		@NotNull TypeContenu type,
		@Size(max = 500) String synopsisCourt,
		@Size(max = 5000) String synopsis,
		@Min(1900) @Max(2100) Integer annee,
		@Positive @Max(1000) Integer dureeMinutes,
		@Pattern(regexp = "^(Tous|10\\+|13\\+|16\\+|18\\+)$", message = "Classification attendue : Tous, 10+, 13+, 16+ ou 18+")
		String classificationAge,
		LocalDate dateSortie,
		@NotEmpty(message = "Choisissez au moins un genre") Set<@NotNull Long> genreIds,
		@NotEmpty(message = "Choisissez au moins un pays")
		Set<@NotNull @Pattern(regexp = "^[A-Z]{2}$", message = "Code pays ISO à 2 lettres attendu") String> paysCodes,
		boolean gratuit,
		boolean exclusivite,
		boolean telechargeable,
		@Size(max = 500) @Pattern(regexp = "^https?://\\S+$", message = "URL de bande-annonce invalide") String bandeAnnonceUrl,
		Long version) {

	public InformationsFilm versInformations() {
		return new InformationsFilm(titre, type, synopsisCourt, synopsis, annee, dureeMinutes, classificationAge,
				dateSortie, gratuit, exclusivite, telechargeable, bandeAnnonceUrl);
	}
}
