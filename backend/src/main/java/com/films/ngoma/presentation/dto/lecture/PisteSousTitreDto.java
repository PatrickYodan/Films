package com.films.ngoma.presentation.dto.lecture;

import com.films.ngoma.domain.SousTitre;

public record PisteSousTitreDto(String langue, String libelle, String url) {

	public PisteSousTitreDto(SousTitre s) {
		this(s.getLangue(), s.getLibelle(), s.getFichierVttUrl());
	}
}
