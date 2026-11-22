package com.films.ngoma.presentation.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Identifiants de connexion.
 */
public record AuthDto(@NotBlank @Email String email, @NotBlank String motDePasse) {
}
