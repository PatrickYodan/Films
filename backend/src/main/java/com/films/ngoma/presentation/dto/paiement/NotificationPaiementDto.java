package com.films.ngoma.presentation.dto.paiement;

/**
 * Notification (webhook) envoyée par l'opérateur de paiement.
 */
public record NotificationPaiementDto(String reference, String referenceOperateur, boolean succes) {
}
