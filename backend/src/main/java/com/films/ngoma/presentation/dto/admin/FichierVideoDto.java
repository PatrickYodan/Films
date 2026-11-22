package com.films.ngoma.presentation.dto.admin;

import com.films.ngoma.domain.FichierVideo;
import com.films.ngoma.enums.Qualite;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * Vidéo d'une qualité : manifeste HLS (.m3u8, recommandé) ou fichier MP4 (.mp4),
 * en chemin relatif à video.cdn.base_url (ex. films/lagos-express/480p/index.m3u8) ou en adresse complète.
 */
public record FichierVideoDto(
		@NotNull Qualite qualite,
		@NotBlank @Size(max = 500)
		@Pattern(regexp = "^(?!.*\\.\\.)(https?://\\S+|[A-Za-z0-9][A-Za-z0-9._/-]*)\\.(m3u8|mp4)$",
				message = "Lien vidéo invalide : un manifeste HLS .m3u8 ou un fichier .mp4, relatif au CDN ou en http(s)")
		String cheminHls,
		@PositiveOrZero Long tailleOctets) {

	public FichierVideoDto(FichierVideo v) {
		this(v.getQualite(), v.getCheminHls(), v.getTailleOctets());
	}
}
