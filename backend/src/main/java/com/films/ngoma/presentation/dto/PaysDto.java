package com.films.ngoma.presentation.dto;

import com.films.ngoma.domain.Pays;

public record PaysDto(String code, String nom, String drapeauUrl) {

	public PaysDto(Pays pays) {
		this(pays.getCode(), pays.getNom(), pays.getDrapeauUrl());
	}
}
