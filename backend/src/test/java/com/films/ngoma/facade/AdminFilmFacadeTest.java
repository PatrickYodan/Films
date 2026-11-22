package com.films.ngoma.facade;

import com.films.ngoma.domain.Film;
import com.films.ngoma.domain.Genre;
import com.films.ngoma.domain.Pays;
import com.films.ngoma.enums.StatutFilm;
import com.films.ngoma.enums.TypeContenu;
import com.films.ngoma.exception.FilmException;
import com.films.ngoma.presentation.dto.admin.FilmAdminDetailDto;
import com.films.ngoma.presentation.dto.admin.FilmEditionDto;
import com.films.ngoma.repository.FilmRepository;
import com.films.ngoma.repository.GenreRepository;
import com.films.ngoma.repository.PaysRepository;
import com.films.ngoma.repository.PersonneRepository;
import com.films.ngoma.service.StockageService;
import com.films.ngoma.utils.ImageUtilsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.unit.DataSize;
import com.films.ngoma.enums.Qualite;
import java.io.ByteArrayInputStream;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Logique de la façade d'administration, dépôts simulés (sans base de données).
 */
class AdminFilmFacadeTest {

	private FilmRepository filmRepository;
	private GenreRepository genreRepository;
	private PaysRepository paysRepository;
	private StockageService stockageService;
	private AdminFilmFacade facade;

	@BeforeEach
	void preparer() {
		filmRepository = mock(FilmRepository.class);
		genreRepository = mock(GenreRepository.class);
		paysRepository = mock(PaysRepository.class);
		stockageService = mock(StockageService.class);
		facade = new AdminFilmFacade(filmRepository, genreRepository, paysRepository, mock(PersonneRepository.class),
				stockageService, mock(PlatformTransactionManager.class), DataSize.ofMegabytes(10));
		when(filmRepository.saveAndFlush(any(Film.class))).thenAnswer(i -> i.getArgument(0));
		when(genreRepository.findAllById(any())).thenReturn(List.of(mock(Genre.class)));
		when(paysRepository.findAllById(any())).thenReturn(List.of(mock(Pays.class)));
	}

	static FilmEditionDto edition(String titre) {
		return new FilmEditionDto(titre, TypeContenu.FILM, null, null, 2025, 95, "Tous", null,
				Set.of(1L), Set.of("CI"), false, false, false, null, null);
	}

	@Test
	void creationEnBrouillonAvecSlugGenere() {
		when(filmRepository.slugsCommencantPar("la-dot")).thenReturn(List.of());
		FilmAdminDetailDto film = facade.creer(edition("La Dot"));
		assertEquals("la-dot", film.slug());
		assertEquals(StatutFilm.BROUILLON, film.statut());
	}

	@Test
	void creationAvecTitreDejaPrisDonneUnSlugUnique() {
		when(filmRepository.slugsCommencantPar("la-dot")).thenReturn(List.of("la-dot", "la-dot-2"));
		assertEquals("la-dot-3", facade.creer(edition("La Dot")).slug());
	}

	@Test
	void genreOuPaysInconnuRefuse() {
		when(filmRepository.slugsCommencantPar(anyString())).thenReturn(List.of());
		when(genreRepository.findAllById(any())).thenReturn(List.of());
		FilmException erreur = assertThrows(FilmException.class, () -> facade.creer(edition("X")));
		assertEquals(7104L, erreur.getApplicationErreur().getCode());
	}

	@Test
	void uploadRefuseSiLeFichierNEstPasUneImage() {
		Film film = Film.creer("la-dot", FilmPublicationTestAcces.informations());
		when(filmRepository.findById(1L)).thenReturn(Optional.of(film));
		MockMultipartFile faux = new MockMultipartFile("fichier", "affiche.png", "image/png", "<html>".getBytes());
		FilmException erreur = assertThrows(FilmException.class, () -> facade.changerAffiche(1L, faux));
		assertEquals(7103L, erreur.getApplicationErreur().getCode());
		verify(stockageService, never()).enregistrer(anyString(), anyString(), any(), anyString());
	}

	@Test
	void uploadAccepteUneVraieImageEtSupprimeLAncienne() {
		Film film = Film.creer("la-dot", FilmPublicationTestAcces.informations());
		film.changerAffiche("http://localhost:8090/medias/films/la-dot/affiche-ancienne.png");
		when(filmRepository.findById(1L)).thenReturn(Optional.of(film));
		when(stockageService.enregistrer(eq("films/la-dot"), startsWith("affiche-"), any(), eq("image/png")))
				.thenReturn("http://localhost:8090/medias/films/la-dot/affiche-nouvelle.png");
		MockMultipartFile image = new MockMultipartFile("fichier", "a.png", "image/png", ImageUtilsTest.PNG);

		FilmAdminDetailDto resultat = facade.changerAffiche(1L, image);

		assertTrue(resultat.afficheUrl().endsWith("affiche-nouvelle.png"));
		verify(stockageService).supprimer("http://localhost:8090/medias/films/la-dot/affiche-ancienne.png");
	}

	@Test
	void versionPerimeeRefusee() {
		Film film = Film.creer("la-dot", FilmPublicationTestAcces.informations());
		when(filmRepository.findById(1L)).thenReturn(Optional.of(film));
		FilmEditionDto perimee = new FilmEditionDto("La Dot", TypeContenu.FILM, null, null, null, null, null, null,
				Set.of(1L), Set.of("CI"), false, false, false, null, 42L);
		assertThrows(org.springframework.orm.ObjectOptimisticLockingFailureException.class,
				() -> facade.modifier(1L, perimee));
		Mockito.verify(filmRepository, never()).saveAndFlush(any());
	}

	/** Début d'un vrai fichier MP4 : taille de boîte puis « ftyp ». */
	static final byte[] MP4 = {0, 0, 0, 0x20, 'f', 't', 'y', 'p', 'i', 's', 'o', 'm', 0, 0, 2, 0};

	@Test
	void envoiVideoRefuseSiCeNEstPasUnMp4() {
		Film film = Film.creer("la-dot", FilmPublicationTestAcces.informations());
		when(filmRepository.findById(1L)).thenReturn(Optional.of(film));
		FilmException erreur = assertThrows(FilmException.class,
				() -> facade.envoyerVideo(1L, Qualite.STANDARD_480P, new ByteArrayInputStream("<html>pas une vidéo</html>".getBytes()), 26L));
		assertEquals(7105L, erreur.getApplicationErreur().getCode());
		verify(stockageService, never()).enregistrerFlux(anyString(), anyString(), any(), org.mockito.ArgumentMatchers.anyLong(), anyString());
	}

	@Test
	void envoiVideoRefuseSiTailleAnnonceeTropGrande() {
		FilmException erreur = assertThrows(FilmException.class,
				() -> facade.envoyerVideo(1L, Qualite.STANDARD_480P, new ByteArrayInputStream(MP4), DataSize.ofMegabytes(11).toBytes()));
		assertEquals(7106L, erreur.getApplicationErreur().getCode());
	}

	@Test
	void envoiVideoMp4AssocieLeFichierALaQualite() {
		Film film = Film.creer("la-dot", FilmPublicationTestAcces.informations());
		when(filmRepository.findById(1L)).thenReturn(Optional.of(film));
		when(stockageService.enregistrerFlux(eq("films/la-dot/videos"), startsWith("standard-480p-"), any(), eq(DataSize.ofMegabytes(10).toBytes()), eq("video/mp4")))
				.thenReturn(new StockageService.FichierStocke("http://localhost:8090/medias/films/la-dot/videos/standard-480p-1.mp4", 16L));

		FilmAdminDetailDto resultat = facade.envoyerVideo(1L, Qualite.STANDARD_480P, new ByteArrayInputStream(MP4), 16L);

		assertEquals(1, resultat.videos().size());
		assertEquals(Qualite.STANDARD_480P, resultat.videos().get(0).qualite());
		assertTrue(resultat.videos().get(0).cheminHls().endsWith(".mp4"));
		assertEquals(16L, resultat.videos().get(0).tailleOctets());
	}

	@Test
	void envoiSousTitreSrtConvertiEtAssocieALaLangue() {
		Film film = Film.creer("la-dot", FilmPublicationTestAcces.informations());
		when(filmRepository.findById(1L)).thenReturn(Optional.of(film));
		when(stockageService.enregistrer(eq("films/la-dot/sous-titres"), startsWith("fr-"), any(), eq("text/vtt")))
				.thenReturn("http://localhost:8090/medias/films/la-dot/sous-titres/fr-1.vtt");
		MockMultipartFile srt = new MockMultipartFile("fichier", "la-dot.srt", "application/x-subrip",
				"1\n00:00:01,000 --> 00:00:02,000\nBonjour\n".getBytes());

		FilmAdminDetailDto resultat = facade.envoyerSousTitre(1L, " FR ", "Français", srt);

		assertEquals(1, resultat.sousTitres().size());
		assertEquals("fr", resultat.sousTitres().get(0).langue());
		assertEquals("Français", resultat.sousTitres().get(0).libelle());
		assertTrue(resultat.sousTitres().get(0).fichierVttUrl().endsWith(".vtt"));
	}

	@Test
	void envoiSousTitreRefuseSiLeFichierNEstPasDesSousTitres() {
		MockMultipartFile faux = new MockMultipartFile("fichier", "fr.vtt", "text/vtt", "<html></html>".getBytes());
		FilmException erreur = assertThrows(FilmException.class, () -> facade.envoyerSousTitre(1L, "fr", "Français", faux));
		assertEquals(7107L, erreur.getApplicationErreur().getCode());
		verify(stockageService, never()).enregistrer(anyString(), anyString(), any(), anyString());
	}

	@Test
	void envoiSousTitreRefuseSansLangueValide() {
		MockMultipartFile vtt = new MockMultipartFile("fichier", "fr.vtt", "text/vtt", "WEBVTT\n".getBytes());
		assertThrows(FilmException.class, () -> facade.envoyerSousTitre(1L, "français", "Français", vtt));
	}

	@Test
	void signatureMp4() {
		assertTrue(AdminFilmFacade.estMp4(MP4));
		assertTrue(!AdminFilmFacade.estMp4("GIF89a......".getBytes()));
	}

	/** Accès aux données de test partagées avec FilmPublicationTest (même paquet que Film). */
	static final class FilmPublicationTestAcces {
		static com.films.ngoma.domain.InformationsFilm informations() {
			return new com.films.ngoma.domain.InformationsFilm("La Dot", TypeContenu.FILM, null, null, 2025, 97,
					"Tous", null, false, false, false, null);
		}
	}
}
