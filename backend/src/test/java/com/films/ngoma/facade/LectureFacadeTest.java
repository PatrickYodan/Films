package com.films.ngoma.facade;

import com.films.ngoma.domain.Abonnement;
import com.films.ngoma.domain.Film;
import com.films.ngoma.domain.InformationsFilm;
import com.films.ngoma.domain.Pass;
import com.films.ngoma.domain.Utilisateur;
import com.films.ngoma.enums.Qualite;
import com.films.ngoma.enums.StatutFilm;
import com.films.ngoma.enums.TypeContenu;
import com.films.ngoma.exception.LectureException;
import com.films.ngoma.presentation.dto.lecture.LectureDto;
import com.films.ngoma.repository.AbonnementRepository;
import com.films.ngoma.repository.FilmRepository;
import com.films.ngoma.service.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

/**
 * Droits de lecture (dépôts simulés).
 */
class LectureFacadeTest {

	private FilmRepository filmRepository;
	private AbonnementRepository abonnementRepository;
	private SecurityService securityService;
	private LectureFacade facade;

	@BeforeEach
	void preparer() {
		filmRepository = mock(FilmRepository.class);
		abonnementRepository = mock(AbonnementRepository.class);
		securityService = mock(SecurityService.class);
		Utilisateur utilisateur = mock(Utilisateur.class);
		when(utilisateur.getId()).thenReturn(1L);
		when(securityService.utilisateurConnecte()).thenReturn(utilisateur);
		facade = new LectureFacade(filmRepository, abonnementRepository, securityService, "https://cdn.exemple.ci/");
	}

	private Film film(boolean gratuit, Qualite... qualites) {
		Film film = Film.creer("mon-film", new InformationsFilm("Mon film", TypeContenu.FILM, null, null, null, null,
				null, null, gratuit, false, false, null));
		Map<Qualite, Film.VideoSaisie> videos = new LinkedHashMap<>();
		for (Qualite q : qualites) {
			videos.put(q, new Film.VideoSaisie("films/mon-film/" + q.name().toLowerCase() + "/index.m3u8", null));
		}
		film.remplacerVideos(videos);
		when(filmRepository.findBySlugAndStatut("mon-film", StatutFilm.PUBLIE)).thenReturn(Optional.of(film));
		return film;
	}

	private void passActif(Qualite qualiteMax) {
		Pass pass = mock(Pass.class);
		when(pass.getQualiteMax()).thenReturn(qualiteMax);
		Abonnement abonnement = mock(Abonnement.class);
		when(abonnement.getPass()).thenReturn(pass);
		when(abonnementRepository.findFirstByUtilisateurIdAndStatutAndDateFinAfterOrderByDateFinDesc(anyLong(), any(), any()))
				.thenReturn(Optional.of(abonnement));
	}

	@Test
	void filmGratuitSansPass() {
		film(true, Qualite.STANDARD_480P);
		LectureDto lecture = facade.ouvrir("mon-film", null);
		assertEquals("https://cdn.exemple.ci/films/mon-film/standard_480p/index.m3u8", lecture.urlHls());
		verify(abonnementRepository, never()).findFirstByUtilisateurIdAndStatutAndDateFinAfterOrderByDateFinDesc(anyLong(), any(), any());
	}

	@Test
	void filmPayantSansPassRefuse() {
		film(false, Qualite.STANDARD_480P);
		when(abonnementRepository.findFirstByUtilisateurIdAndStatutAndDateFinAfterOrderByDateFinDesc(anyLong(), any(), any()))
				.thenReturn(Optional.empty());
		LectureException erreur = assertThrows(LectureException.class, () -> facade.ouvrir("mon-film", null));
		assertEquals(HttpStatus.FORBIDDEN, erreur.getStatut());
		assertEquals(7401L, erreur.getApplicationErreur().getCode());
	}

	@Test
	void filmPayantAvecPassEtQualiteDemandee() {
		film(false, Qualite.ECO_240P, Qualite.STANDARD_480P, Qualite.HD_1080P);
		passActif(Qualite.HD_1080P);
		LectureDto lecture = facade.ouvrir("mon-film", Qualite.HD_1080P);
		assertEquals(Qualite.HD_1080P, lecture.qualite());
		assertEquals(List.of(Qualite.ECO_240P, Qualite.STANDARD_480P, Qualite.HD_1080P), lecture.qualitesDisponibles());
	}

	@Test
	void qualitePlafonneeParLePass() {
		film(false, Qualite.ECO_240P, Qualite.STANDARD_480P, Qualite.HD_1080P);
		passActif(Qualite.STANDARD_480P);
		LectureDto lecture = facade.ouvrir("mon-film", Qualite.HD_1080P);
		assertEquals(Qualite.STANDARD_480P, lecture.qualite());
		assertEquals(List.of(Qualite.ECO_240P, Qualite.STANDARD_480P), lecture.qualitesDisponibles());
	}

	@Test
	void qualiteAbsenteRemplaceeParLaPlusProche() {
		film(true, Qualite.ECO_240P, Qualite.HD_1080P);
		assertEquals(Qualite.ECO_240P, facade.ouvrir("mon-film", Qualite.STANDARD_480P).qualite());
	}

	@Test
	void adresseHttpsCompleteGardeeTelleQuelle() {
		assertEquals("https://autre.cdn/x.m3u8", facade.resoudre("https://autre.cdn/x.m3u8"));
		assertEquals("https://cdn.exemple.ci/a/b.m3u8", facade.resoudre("/a/b.m3u8"));
		assertEquals("http://localhost:8090/medias/films/x/videos/a.mp4", facade.resoudre("http://localhost:8090/medias/films/x/videos/a.mp4"));
	}

	@Test
	void formatSelonLExtension() {
		assertEquals("MP4", LectureFacade.format("http://localhost:8090/medias/films/x/videos/a.mp4"));
		assertEquals("MP4", LectureFacade.format("https://cdn.exemple.ci/film.MP4?jeton=abc"));
		assertEquals("HLS", LectureFacade.format("https://cdn.exemple.ci/film/index.m3u8"));
	}

	@Test
	void sansVideoIndisponible() {
		film(true);
		LectureException erreur = assertThrows(LectureException.class, () -> facade.ouvrir("mon-film", null));
		assertEquals(HttpStatus.NOT_FOUND, erreur.getStatut());
	}
}
