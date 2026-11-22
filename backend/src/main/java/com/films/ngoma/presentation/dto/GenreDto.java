package com.films.ngoma.presentation.dto;

import com.films.ngoma.domain.Genre;

public record GenreDto(Long id, String nom) {

	public GenreDto(Genre genre) {
		this(genre.getId(), genre.getNom());
	}
}
