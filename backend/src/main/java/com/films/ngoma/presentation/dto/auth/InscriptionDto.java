package com.films.ngoma.presentation.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Données de création d'un compte abonné.
 */
public record InscriptionDto(
		@NotBlank @Size(max = 150) String nom,
		@NotBlank @Email @Size(max = 255) String email,
		@Pattern(regexp = "^\\+?[0-9 ]{8,20}$", message = "Numéro de téléphone invalide") String telephone,
		@NotBlank @Size(min = 8, max = 100) String motDePasse) {
}
