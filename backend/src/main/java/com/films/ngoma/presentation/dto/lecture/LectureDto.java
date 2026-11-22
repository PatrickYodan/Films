package com.films.ngoma.presentation.dto.lecture;

import com.films.ngoma.enums.Qualite;

import java.util.List;

/**
 * Autorisation de lecture : adresse du flux HLS et pistes disponibles.
 *
 * @param urlHls              adresse du manifeste HLS à lire
 * @param qualite             qualité effectivement servie (plafonnée par le pass)
 * @param qualitesDisponibles qualités que l'utilisateur peut choisir
 * @param format              « HLS » (manifeste .m3u8, lu avec hls.js) ou « MP4 » (fichier lu directement)
 */
public record LectureDto(String urlHls, Qualite qualite, List<Qualite> qualitesDisponibles,
						 List<PisteSousTitreDto> sousTitres, String format) {
}
