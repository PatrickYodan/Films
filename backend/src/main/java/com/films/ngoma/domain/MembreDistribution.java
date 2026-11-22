package com.films.ngoma.domain;

import com.films.ngoma.enums.Fonction;

/**
 * Ligne de distribution à appliquer à un film ({@link Film#remplacerDistribution}).
 * La personne doit déjà être enregistrée (identifiant non nul).
 */
public record MembreDistribution(Personne personne, Fonction fonction, String role) {
}
