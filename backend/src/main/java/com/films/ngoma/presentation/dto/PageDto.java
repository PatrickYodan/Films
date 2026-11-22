package com.films.ngoma.presentation.dto;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Page de résultats sérialisable de façon stable.
 */
public record PageDto<T>(List<T> contenu, int page, int taille, long total, int totalPages) {

	public PageDto(Page<T> page) {
		this(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
	}
}
