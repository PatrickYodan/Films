package com.films.ngoma.presentation.dto.admin;

import com.films.ngoma.domain.SousTitre;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SousTitreDto(
		@NotBlank @Pattern(regexp = "^[a-zA-Z]{2,3}(-[a-zA-Z]{2})?$", message = "Code langue attendu (ex. fr, en, wo)")
		String langue,
		@NotBlank @Size(max = 50) String libelle,
		@NotBlank @Size(max = 500)
		@Pattern(regexp = "^https?://\\S+\\.vtt(\\?\\S*)?$", message = "URL d'un fichier .vtt attendue")
		String fichierVttUrl) {

	public SousTitreDto(SousTitre s) {
		this(s.getLangue(), s.getLibelle(), s.getFichierVttUrl());
	}
}
