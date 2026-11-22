package com.films.ngoma.presentation.dto;

import com.films.ngoma.domain.Pass;
import com.films.ngoma.enums.Qualite;

public record PassDto(Long id, String code, String nom, int dureeHeures, int prixFcfa, int ecransMax,
					  Qualite qualiteMax, String etiquette, String description) {

	public PassDto(Pass pass) {
		this(pass.getId(), pass.getCode(), pass.getNom(), pass.getDureeHeures(), pass.getPrixFcfa(),
				pass.getEcransMax(), pass.getQualiteMax(), pass.getEtiquette(), pass.getDescription());
	}
}
