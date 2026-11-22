package com.films.ngoma.presentation.dto.film;

/**
 * Critères de recherche du catalogue (tous optionnels).
 */
public record RechercheFilmDto(String motCle, Long genreId, String paysCode, Boolean gratuit) {
}
