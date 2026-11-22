package com.films.ngoma.presentation.dto.admin;

import com.films.ngoma.domain.Participation;
import com.films.ngoma.enums.Fonction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * Ligne de distribution. En entrée, {@code ordre} est facultatif (à défaut : ordre de la liste).
 */
public record ParticipationDto(@NotBlank @Size(max = 150) String nom,
							   @NotNull Fonction fonction,
							   @Size(max = 150) String role,
							   @PositiveOrZero Integer ordre) {

	public ParticipationDto(Participation p) {
		this(p.getPersonne().getNom(), p.getFonction(), p.getRole(), p.getOrdre());
	}
}
