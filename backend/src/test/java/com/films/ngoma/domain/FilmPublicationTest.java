package com.films.ngoma.domain;

import com.films.ngoma.enums.Qualite;
import com.films.ngoma.enums.StatutFilm;
import com.films.ngoma.enums.TypeContenu;
import com.films.ngoma.exception.FilmException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Règles de publication d'un film (sans base de données).
 */
class FilmPublicationTest {

	static InformationsFilm informations(String synopsis) {
		return new InformationsFilm("Mon film", TypeContenu.FILM, null, synopsis, 2025, 90, "13+", null,
				false, false, false, null);
	}

	@Test
	void unFilmCreeEstUnBrouillon() {
		Film film = Film.creer("mon-film", informations(null));
		assertEquals(StatutFilm.BROUILLON, film.getStatut());
		assertEquals("mon-film", film.getSlug());
	}

	@Test
	void publicationRefuseeSiIncomplet() {
		Film film = Film.creer("mon-film", informations(null));
		FilmException erreur = assertThrows(FilmException.class, film::publier);
		assertEquals(7102L, erreur.getApplicationErreur().getCode());
		assertTrue(erreur.getMessage().contains("l'affiche"), erreur.getMessage());
		assertTrue(erreur.getMessage().contains("au moins une vidéo"), erreur.getMessage());
		assertEquals(StatutFilm.BROUILLON, film.getStatut());
	}

	@Test
	void unSynopsisVideNeCompteParPour() {
		Film film = filmComplet();
		film.modifier(informations("   "));
		assertEquals(List.of("le synopsis"), film.elementsManquantsPourPublication());
	}

	@Test
	void publicationAccepteeSiComplet() {
		Film film = filmComplet();
		assertTrue(film.elementsManquantsPourPublication().isEmpty());
		film.publier();
		assertEquals(StatutFilm.PUBLIE, film.getStatut());
		film.depublier();
		assertEquals(StatutFilm.BROUILLON, film.getStatut());
	}

	@Test
	void remplacerVideosMetAJourSurPlaceEtRetireLesQualitesAbsentes() {
		Film film = filmComplet();
		FichierVideo avant = film.getVideos().get(0);
		film.remplacerVideos(Map.of(Qualite.STANDARD_480P, new Film.VideoSaisie("films/x/480p/v2.m3u8", 10L)));
		assertEquals(1, film.getVideos().size());
		assertTrue(film.getVideos().get(0) == avant, "la ligne existante doit être réutilisée");
		assertEquals("films/x/480p/v2.m3u8", avant.getCheminHls());
		film.remplacerVideos(Map.of());
		assertTrue(film.getVideos().isEmpty());
	}

	static Film filmComplet() {
		Film film = Film.creer("mon-film", informations("Un synopsis."));
		film.changerAffiche("https://cdn.example.com/a.jpg");
		film.changerBandeau("https://cdn.example.com/b.jpg");
		film.remplacerVideos(Map.of(Qualite.STANDARD_480P, new Film.VideoSaisie("films/x/480p/index.m3u8", null)));
		return film;
	}
}
