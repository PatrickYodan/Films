package com.films.ngoma.presentation.dto.compte;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangementMotDePasseDto(@NotBlank String motDePasseActuel,
									  @NotBlank @Size(min = 8, max = 100) String nouveauMotDePasse) {
}
