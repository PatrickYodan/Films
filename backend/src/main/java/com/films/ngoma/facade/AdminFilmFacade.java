package com.films.ngoma.facade;

import com.films.ngoma.domain.Film;
import com.films.ngoma.domain.Genre;
import com.films.ngoma.domain.MembreDistribution;
import com.films.ngoma.domain.Pays;
import com.films.ngoma.domain.Personne;
import com.films.ngoma.enums.Qualite;
import com.films.ngoma.enums.StatutFilm;
import com.films.ngoma.exception.FilmException;
import com.films.ngoma.presentation.dto.PageDto;
import com.films.ngoma.presentation.dto.admin.FichierVideoDto;
import com.films.ngoma.presentation.dto.admin.FilmAdminDetailDto;
import com.films.ngoma.presentation.dto.admin.FilmAdminResumeDto;
import com.films.ngoma.presentation.dto.admin.FilmEditionDto;
import com.films.ngoma.presentation.dto.admin.ParticipationDto;
import com.films.ngoma.presentation.dto.admin.SousTitreDto;
import com.films.ngoma.repository.FilmRepository;
import com.films.ngoma.repository.GenreRepository;
import com.films.ngoma.repository.PaysRepository;
import com.films.ngoma.repository.PersonneRepository;
import com.films.ngoma.service.StockageService;
import com.films.ngoma.utils.ImageUtils;
import com.films.ngoma.utils.SlugUtils;
import com.films.ngoma.utils.SousTitreUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Gestion des films par l'équipe contenu (back-office). Réservée aux administrateurs.
 */
@Service
public class AdminFilmFacade {

	/** Taille maximale d'une affiche ou d'un bandeau : 5 Mo. */
	public static final long TAILLE_MAX_IMAGE = 5L * 1024 * 1024;
	private static final SecureRandom ALEA = new SecureRandom();

	private final FilmRepository filmRepository;
	private final GenreRepository genreRepository;
	private final PaysRepository paysRepository;
	private final PersonneRepository personneRepository;
	private final StockageService stockageService;
	private final TransactionTemplate transaction;
	private final DataSize tailleMaxVideo;

	public AdminFilmFacade(FilmRepository filmRepository, GenreRepository genreRepository, PaysRepository paysRepository,
						   PersonneRepository personneRepository, StockageService stockageService,
						   PlatformTransactionManager transactionManager,
						   @Value("${ngoma.video.taille_max:4GB}") DataSize tailleMaxVideo) {
		this.filmRepository = filmRepository;
		this.genreRepository = genreRepository;
		this.paysRepository = paysRepository;
		this.personneRepository = personneRepository;
		this.stockageService = stockageService;
		this.transaction = new TransactionTemplate(transactionManager);
		this.tailleMaxVideo = tailleMaxVideo;
	}

	/**
	 * Tous les films (tous statuts), du plus récemment modifié au plus ancien.
	 */
	@Transactional(readOnly = true)
	public PageDto<FilmAdminResumeDto> lister(String motCle, StatutFilm statut, Pageable pageable) {
		String motif = motCle == null || motCle.isBlank() ? "%" : "%" + motCle.trim().toLowerCase(Locale.ROOT) + "%";
		return new PageDto<>(filmRepository.rechercherAdmin(motif, statut, pageable).map(FilmAdminResumeDto::new));
	}

	@Transactional(readOnly = true)
	public FilmAdminDetailDto detail(Long id) {
		return new FilmAdminDetailDto(charger(id));
	}

	/**
	 * Crée un film en brouillon, avec un slug unique généré à partir du titre.
	 */
	@Transactional
	public FilmAdminDetailDto creer(FilmEditionDto dto) {
		String base = SlugUtils.slugifier(dto.titre());
		String slug = SlugUtils.rendreUnique(base, filmRepository.slugsCommencantPar(base));
		Film film = Film.creer(slug, dto.versInformations());
		appliquerReferentiels(film, dto);
		return enregistrer(film);
	}

	@Transactional
	public FilmAdminDetailDto modifier(Long id, FilmEditionDto dto) {
		Film film = charger(id);
		if (!Objects.equals(dto.version(), film.getVersion())) {
			// Quelqu'un a modifié le film depuis son chargement : renvoie 409 (voir ExceptionHandlers).
			throw new ObjectOptimisticLockingFailureException(Film.class, id);
		}
		film.modifier(dto.versInformations());
		appliquerReferentiels(film, dto);
		return enregistrer(film);
	}

	/**
	 * Supprime le film et ses images. Distribution, vidéos, sous-titres, favoris et visionnages
	 * sont supprimés en cascade ; les personnes restent (partagées entre les films).
	 */
	@Transactional
	public void supprimer(Long id) {
		Film film = charger(id);
		String affiche = film.getAfficheUrl();
		String bandeau = film.getBandeauUrl();
		List<String> videos = film.getVideos().stream().map(v -> v.getCheminHls()).toList();
		List<String> sousTitres = film.getSousTitres().stream().map(s -> s.getFichierVttUrl()).toList();
		filmRepository.delete(film);
		filmRepository.flush();
		stockageService.supprimer(affiche);
		stockageService.supprimer(bandeau);
		// Seuls les fichiers envoyés sur notre stockage sont supprimés (les liens externes sont ignorés).
		videos.forEach(stockageService::supprimer);
		sousTitres.forEach(stockageService::supprimer);
	}

	@Transactional
	public FilmAdminDetailDto publier(Long id) {
		Film film = charger(id);
		film.publier();
		return enregistrer(film);
	}

	@Transactional
	public FilmAdminDetailDto depublier(Long id) {
		Film film = charger(id);
		film.depublier();
		return enregistrer(film);
	}

	@Transactional
	public FilmAdminDetailDto changerAffiche(Long id, MultipartFile fichier) {
		Film film = charger(id);
		String ancienne = film.getAfficheUrl();
		film.changerAffiche(stockerImage(film, "affiche", fichier));
		FilmAdminDetailDto resultat = enregistrer(film);
		stockageService.supprimer(ancienne);
		return resultat;
	}

	@Transactional
	public FilmAdminDetailDto changerBandeau(Long id, MultipartFile fichier) {
		Film film = charger(id);
		String ancien = film.getBandeauUrl();
		film.changerBandeau(stockerImage(film, "bandeau", fichier));
		FilmAdminDetailDto resultat = enregistrer(film);
		stockageService.supprimer(ancien);
		return resultat;
	}

	/**
	 * Remplace la distribution. Les personnes sont retrouvées par nom (sans tenir compte de la casse)
	 * ou créées si elles n'existent pas.
	 */
	@Transactional
	public FilmAdminDetailDto remplacerDistribution(Long id, List<ParticipationDto> lignes) {
		Film film = charger(id);
		List<ParticipationDto> triees = IntStream.range(0, lignes.size()).boxed()
				.sorted(Comparator.comparing((Integer i) -> lignes.get(i).ordre() == null ? i : lignes.get(i).ordre())
						.thenComparing(i -> i))
				.map(lignes::get)
				.toList();
		List<MembreDistribution> membres = triees.stream()
				.map(l -> new MembreDistribution(trouverOuCreerPersonne(l.nom()), l.fonction(), l.role()))
				.toList();
		film.remplacerDistribution(membres);
		return enregistrer(film);
	}

	@Transactional
	public FilmAdminDetailDto remplacerVideos(Long id, List<FichierVideoDto> lignes) {
		Film film = charger(id);
		Set<String> anciennes = film.getVideos().stream().map(v -> v.getCheminHls()).collect(Collectors.toSet());
		Map<Qualite, Film.VideoSaisie> parQualite = new LinkedHashMap<>();
		lignes.forEach(l -> parQualite.put(l.qualite(), new Film.VideoSaisie(l.cheminHls().trim(), l.tailleOctets())));
		film.remplacerVideos(parQualite);
		FilmAdminDetailDto resultat = enregistrer(film);
		// Fichiers envoyés qui ne sont plus utilisés par aucune qualité
		anciennes.removeAll(parQualite.values().stream().map(Film.VideoSaisie::cheminHls).collect(Collectors.toSet()));
		anciennes.forEach(stockageService::supprimer);
		return resultat;
	}

	/**
	 * Envoi d'un fichier vidéo MP4 pour une qualité. Le fichier est copié par blocs sur le stockage, hors transaction
	 * (un film pèse plusieurs Go : on ne bloque pas de connexion à la base pendant l'envoi), puis associé au film.
	 * Solution de développement et de test : en production, l'envoi se fera vers un service vidéo (Bunny, Cloudflare…).
	 */
	public FilmAdminDetailDto envoyerVideo(Long id, Qualite qualite, InputStream flux, Long tailleAnnoncee) {
		long tailleMax = tailleMaxVideo.toBytes();
		if (tailleAnnoncee != null && tailleAnnoncee > tailleMax) {
			throw FilmException.videoTropVolumineuse(tailleMaxVideo.toString());
		}
		String slug = transaction.execute(statut -> charger(id).getSlug());

		BufferedInputStream entree = new BufferedInputStream(flux);
		try {
			entree.mark(16);
			byte[] entete = entree.readNBytes(12);
			entree.reset();
			if (!estMp4(entete)) {
				throw FilmException.videoInvalide();
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

		byte[] alea = new byte[4];
		ALEA.nextBytes(alea);
		String nom = qualite.name().toLowerCase(Locale.ROOT).replace('_', '-') + "-" + HexFormat.of().formatHex(alea) + ".mp4";
		StockageService.FichierStocke fichier;
		try {
			fichier = stockageService.enregistrerFlux("films/" + slug + "/videos", nom, entree, tailleMax, "video/mp4");
		} catch (StockageService.TailleMaximaleDepassee e) {
			throw FilmException.videoTropVolumineuse(tailleMaxVideo.toString());
		}

		String[] ancienne = new String[1];
		try {
			FilmAdminDetailDto resultat = transaction.execute(statut -> {
				Film film = charger(id);
				film.getVideos().stream().filter(v -> v.getQualite() == qualite).findFirst()
						.ifPresent(v -> ancienne[0] = v.getCheminHls());
				film.definirVideo(qualite, fichier.url(), fichier.taille());
				return enregistrer(film);
			});
			if (ancienne[0] != null && !ancienne[0].equals(fichier.url())) {
				stockageService.supprimer(ancienne[0]);
			}
			return resultat;
		} catch (RuntimeException e) {
			stockageService.supprimer(fichier.url());
			throw e;
		}
	}

	/** Signature MP4 / MOV : la boîte « ftyp » aux octets 4 à 8. */
	static boolean estMp4(byte[] entete) {
		return entete.length >= 8 && "ftyp".equals(new String(entete, 4, 4, StandardCharsets.US_ASCII));
	}

	@Transactional
	public FilmAdminDetailDto remplacerSousTitres(Long id, List<SousTitreDto> lignes) {
		Film film = charger(id);
		Set<String> anciens = film.getSousTitres().stream().map(s -> s.getFichierVttUrl()).collect(Collectors.toSet());
		film.remplacerSousTitres(lignes.stream()
				.map(l -> new Film.SousTitreSaisi(l.langue(), l.libelle(), l.fichierVttUrl()))
				.toList());
		FilmAdminDetailDto resultat = enregistrer(film);
		// Fichiers envoyés qui ne sont plus utilisés
		anciens.removeAll(lignes.stream().map(l -> l.fichierVttUrl().trim()).collect(Collectors.toSet()));
		anciens.forEach(stockageService::supprimer);
		return resultat;
	}

	/** Taille maximale d'un fichier de sous-titres (un film entier pèse généralement moins de 200 Ko). */
	public static final long TAILLE_MAX_SOUS_TITRE = 1024L * 1024;
	private static final java.util.regex.Pattern LANGUE = java.util.regex.Pattern.compile("^[a-z]{2,3}(-[a-z]{2})?$");

	/**
	 * Envoi d'un fichier de sous-titres (.vtt, ou .srt converti en WebVTT) pour une langue.
	 * Remplace la piste existante de cette langue.
	 */
	@Transactional
	public FilmAdminDetailDto envoyerSousTitre(Long id, String langue, String libelle, MultipartFile fichier) {
		String code = langue == null ? "" : langue.trim().toLowerCase(Locale.ROOT);
		if (!LANGUE.matcher(code).matches() || libelle == null || libelle.isBlank() || libelle.length() > 50
				|| fichier == null || fichier.isEmpty() || fichier.getSize() > TAILLE_MAX_SOUS_TITRE) {
			throw FilmException.sousTitreInvalide();
		}
		String vtt;
		try {
			vtt = SousTitreUtils.versWebVtt(fichier.getBytes());
		} catch (IOException | IllegalArgumentException e) {
			throw FilmException.sousTitreInvalide();
		}
		Film film = charger(id);
		String ancien = film.getSousTitres().stream().filter(s -> s.getLangue().equals(code)).findFirst()
				.map(s -> s.getFichierVttUrl()).orElse(null);
		byte[] alea = new byte[4];
		ALEA.nextBytes(alea);
		String url = stockageService.enregistrer("films/" + film.getSlug() + "/sous-titres",
				code + "-" + HexFormat.of().formatHex(alea) + ".vtt", vtt.getBytes(StandardCharsets.UTF_8), "text/vtt");
		try {
			film.definirSousTitre(code, libelle.trim(), url);
			FilmAdminDetailDto resultat = enregistrer(film);
			if (ancien != null && !ancien.equals(url)) {
				stockageService.supprimer(ancien);
			}
			return resultat;
		} catch (RuntimeException e) {
			stockageService.supprimer(url);
			throw e;
		}
	}

	// ------------------------------------------------------------------ outils

	private Film charger(Long id) {
		return filmRepository.findById(id).orElseThrow(() -> FilmException.filmIntrouvable(id));
	}

	/** Enregistre et synchronise avec la base pour renvoyer la version à jour (verrou optimiste côté client). */
	private FilmAdminDetailDto enregistrer(Film film) {
		return new FilmAdminDetailDto(filmRepository.saveAndFlush(film));
	}

	private void appliquerReferentiels(Film film, FilmEditionDto dto) {
		List<Genre> genres = genreRepository.findAllById(dto.genreIds());
		List<Pays> pays = paysRepository.findAllById(dto.paysCodes());
		if (genres.size() != dto.genreIds().size() || pays.size() != dto.paysCodes().size()) {
			throw FilmException.referentielInconnu();
		}
		film.remplacerGenres(genres);
		film.remplacerPays(pays);
	}

	private Personne trouverOuCreerPersonne(String nom) {
		String normalise = nom.trim().replaceAll("\\s+", " ");
		return personneRepository.findFirstByNomIgnoreCaseOrderByIdAsc(normalise)
				.orElseGet(() -> personneRepository.save(new Personne(normalise)));
	}

	/**
	 * Vérifie l'image (signature + taille) puis la stocke sous un nom unique, pour éviter les caches périmés.
	 */
	private String stockerImage(Film film, String prefixe, MultipartFile fichier) {
		if (fichier == null || fichier.isEmpty() || fichier.getSize() > TAILLE_MAX_IMAGE) {
			throw FilmException.imageInvalide();
		}
		byte[] contenu;
		try {
			contenu = fichier.getBytes();
		} catch (IOException e) {
			throw FilmException.imageInvalide();
		}
		String type = ImageUtils.detecterType(contenu);
		if (type == null) {
			throw FilmException.imageInvalide();
		}
		byte[] alea = new byte[4];
		ALEA.nextBytes(alea);
		String nom = prefixe + "-" + HexFormat.of().formatHex(alea) + "." + ImageUtils.extension(type);
		return stockageService.enregistrer("films/" + film.getSlug(), nom, contenu, type);
	}
}
