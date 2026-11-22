package com.films.ngoma.presentation.dto.paiement;

import com.films.ngoma.enums.MoyenPaiement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Demande de paiement d'un pass. Le téléphone est obligatoire pour le Mobile Money.
 */
public record InitierPaiementDto(@NotBlank String codePass, @NotNull MoyenPaiement moyen, String telephone) {
}
