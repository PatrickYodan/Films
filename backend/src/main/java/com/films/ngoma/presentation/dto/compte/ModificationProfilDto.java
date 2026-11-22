package com.films.ngoma.presentation.dto.compte;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ModificationProfilDto(
		@NotBlank @Size(max = 150) String nom,
		@Pattern(regexp = "^\\+?[0-9 ]{8,20}$", message = "Numéro de téléphone invalide") String telephone) {
}
