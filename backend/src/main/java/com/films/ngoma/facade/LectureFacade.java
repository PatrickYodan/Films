package com.films.ngoma.facade;

import com.films.ngoma.domain.FichierVideo;
import com.films.ngoma.domain.Film;
import com.films.ngoma.enums.Qualite;
import com.films.ngoma.enums.StatutAbonnement;
import com.films.ngoma.enums.StatutFilm;
import com.films.ngoma.exception.FilmException;
import com.films.ngoma.exception.LectureException;
import com.films.ngoma.presentation.dto.lecture.LectureDto;
import com.films.ngoma.presentation.dto.lecture.PisteSousTitreDto;
import com.films.ngoma.repository.AbonnementRepository;
import com.films.ngoma.repository.FilmRepository;
import com.films.ngoma.service.SecurityService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * Ouverture de la lecture d'un film : contrôle des droits puis remise de l'adresse du flux.
 * Règle : film gratuit, ou pass actif (la qualité est plafonnée par le pass).
 */
@Service
public class LectureFacade {

	private final FilmRepository filmRepository;
	private final AbonnementRepository abonnementRepository;
	private final SecurityService securityService;
	private final String baseCdn;

	public LectureFacade(FilmRepository filmRepository, AbonnementRepository abonnementRepository,
						 SecurityService securityService, @Value("${video.cdn.base_url}") String baseCdn) {
		this.filmRepository = filmRepository;
		this.abonnementRepository = abonnementRepository;
		this.securityService = securityService;
		this.baseCdn = baseCdn.replaceAll("/+$", "");
	}

	@Transactional(readOnly = true)
	public LectureDto ouvrir(String slug, Qualite demandee) {
		Film film = filmRepository.findBySlugAndStatut(slug, StatutFilm.PUBLIE)
				.orElseThrow(() -> FilmException.filmInconnu(slug));

		Qualite maximum = Qualite.HD_1080P;
		if (!film.isGratuit()) {
			Long utilisateurId = securityService.utilisateurConnecte().getId();
			maximum = abonnementRepository
					.findFirstByUtilisateurIdAndStatutAndDateFinAfterOrderByDateFinDesc(
							utilisateurId, StatutAbonnement.ACTIF, LocalDateTime.now())
					.orElseThrow(LectureException::passNecessaire)
					.getPass().getQualiteMax();
		}

		Qualite plafond = maximum;
		List<FichierVideo> autorisees = film.getVideos().stream()
				.filter(v -> v.getQualite().ordinal() <= plafond.ordinal())
				.sorted(Comparator.comparing(FichierVideo::getQualite))
				.toList();
		if (autorisees.isEmpty()) {
			throw LectureException.videoIndisponible();
		}

		// La qualité demandée (Standard par défaut), sinon la meilleure disponible en dessous, sinon la plus basse.
		Qualite cible = demandee == null ? Qualite.STANDARD_480P : demandee;
		FichierVideo choisie = autorisees.stream()
				.filter(v -> v.getQualite().ordinal() <= cible.ordinal())
				.reduce((a, b) -> b)
				.orElse(autorisees.get(0));

		String url = resoudre(choisie.getCheminHls());
		return new LectureDto(url, choisie.getQualite(),
				autorisees.stream().map(FichierVideo::getQualite).toList(),
				film.getSousTitres().stream().map(PisteSousTitreDto::new).toList(),
				format(url));
	}

	/**
	 * Chemin relatif → adresse sur le CDN ({@code video.cdn.base_url}) ; une adresse https complète est gardée telle quelle.
	 * Les URL signées et expirantes seront produites ici quand le CDN sera choisi.
	 */
	String resoudre(String chemin) {
		return chemin.matches("^https?://.*") ? chemin : baseCdn + "/" + chemin.replaceAll("^/+", "");
	}

	/** « MP4 » pour un fichier .mp4 (paramètres d'URL ignorés), sinon « HLS ». */
	static String format(String url) {
		return url.replaceAll("[?#].*$", "").toLowerCase().endsWith(".mp4") ? "MP4" : "HLS";
	}
}
