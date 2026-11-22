package com.films.ngoma.domain;

import com.films.ngoma.enums.Qualite;
import com.films.ngoma.enums.StatutFilm;
import com.films.ngoma.enums.TypeContenu;
import com.films.ngoma.exception.FilmException;
import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@Entity
@Access(AccessType.FIELD)
@Table(name = Film.TABLE_NAME)
public class Film extends AbstractEntity {

	public static final String TABLE_NAME = "film";
	public static final String TABLE_ID = TABLE_NAME + ID;
	public static final String TABLE_SEQ = TABLE_ID + SEQ;

	@Id
	@SequenceGenerator(name = TABLE_SEQ, sequenceName = TABLE_SEQ, allocationSize = 50)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABLE_SEQ)
	private Long id;

	private String titre;
	private String slug;

	@Column(name = "synopsis_court")
	private String synopsisCourt;

	private String synopsis;
	private Integer annee;

	@Column(name = "duree_minutes")
	private Integer dureeMinutes;

	@Column(name = "classification_age")
	private String classificationAge;

	@Enumerated(EnumType.STRING)
	private TypeContenu type;

	private boolean gratuit;
	private boolean exclusivite;
	private boolean telechargeable;

	@Column(name = "affiche_url")
	private String afficheUrl;

	@Column(name = "bandeau_url")
	private String bandeauUrl;

	@Column(name = "bande_annonce_url")
	private String bandeAnnonceUrl;

	@Column(name = "date_sortie")
	private LocalDate dateSortie;

	@ManyToMany
	@BatchSize(size = 50)
	@JoinTable(name = "film_genre",
			joinColumns = @JoinColumn(name = "film_id"),
			inverseJoinColumns = @JoinColumn(name = "genre_id"))
	private Set<Genre> genres = new LinkedHashSet<>();

	@ManyToMany
	@BatchSize(size = 50)
	@JoinTable(name = "film_pays",
			joinColumns = @JoinColumn(name = "film_id"),
			inverseJoinColumns = @JoinColumn(name = "pays_code"))
	private Set<Pays> pays = new LinkedHashSet<>();

	@Enumerated(EnumType.STRING)
	private StatutFilm statut;

	@OneToMany(mappedBy = "film", cascade = CascadeType.ALL, orphanRemoval = true)
	@BatchSize(size = 50)
	@OrderBy("ordre ASC")
	private List<Participation> distribution = new ArrayList<>();

	@OneToMany(mappedBy = "film", cascade = CascadeType.ALL, orphanRemoval = true)
	@BatchSize(size = 50)
	private List<FichierVideo> videos = new ArrayList<>();

	@OneToMany(mappedBy = "film", cascade = CascadeType.ALL, orphanRemoval = true)
	@BatchSize(size = 50)
	private List<SousTitre> sousTitres = new ArrayList<>();

	protected Film() {
	}

	// ------------------------------------------------------------------ cycle de vie

	/**
	 * Crée un film en brouillon. Le slug (unique) est calculé par l'appelant.
	 */
	public static Film creer(String slug, InformationsFilm informations) {
		Film film = new Film();
		film.slug = slug;
		film.statut = StatutFilm.BROUILLON;
		film.modifier(informations);
		return film;
	}

	/**
	 * Met à jour les informations éditoriales. Le slug ne change pas, pour ne pas casser les liens partagés.
	 */
	public void modifier(InformationsFilm i) {
		this.titre = i.titre().trim();
		this.type = i.type() == null ? TypeContenu.FILM : i.type();
		this.synopsisCourt = vide(i.synopsisCourt());
		this.synopsis = vide(i.synopsis());
		this.annee = i.annee();
		this.dureeMinutes = i.dureeMinutes();
		this.classificationAge = vide(i.classificationAge());
		this.dateSortie = i.dateSortie();
		this.gratuit = i.gratuit();
		this.exclusivite = i.exclusivite();
		this.telechargeable = i.telechargeable();
		this.bandeAnnonceUrl = vide(i.bandeAnnonceUrl());
	}

	/**
	 * Publie le film dans le catalogue.
	 *
	 * @throws FilmException si des éléments obligatoires manquent
	 */
	public void publier() {
		List<String> manquants = elementsManquantsPourPublication();
		if (!manquants.isEmpty()) {
			throw FilmException.filmIncomplet(manquants);
		}
		this.statut = StatutFilm.PUBLIE;
	}

	/** Retire le film du catalogue public (il redevient brouillon). */
	public void depublier() {
		this.statut = StatutFilm.BROUILLON;
	}

	/**
	 * @return les éléments obligatoires manquants pour publier (vide si le film est complet).
	 */
	public List<String> elementsManquantsPourPublication() {
		List<String> manquants = new ArrayList<>();
		if (afficheUrl == null) {
			manquants.add("l'affiche");
		}
		if (bandeauUrl == null) {
			manquants.add("le bandeau");
		}
		if (synopsis == null) {
			manquants.add("le synopsis");
		}
		if (videos.isEmpty()) {
			manquants.add("au moins une vidéo");
		}
		return manquants;
	}

	public boolean estPublie() {
		return statut == StatutFilm.PUBLIE;
	}

	// ------------------------------------------------------------------ images

	public void changerAffiche(String url) {
		this.afficheUrl = vide(url);
	}

	public void changerBandeau(String url) {
		this.bandeauUrl = vide(url);
	}

	// ------------------------------------------------------------------ référentiels

	public void remplacerGenres(Collection<Genre> nouveaux) {
		genres.clear();
		genres.addAll(nouveaux);
	}

	public void remplacerPays(Collection<Pays> nouveaux) {
		pays.clear();
		pays.addAll(nouveaux);
	}

	// ------------------------------------------------------------------ compositions
	// Les lignes existantes sont mises à jour sur place plutôt que supprimées puis recréées :
	// Hibernate exécute les INSERT avant les DELETE, ce qui violerait les clés / contraintes d'unicité.

	/**
	 * Remplace la distribution ; l'ordre d'affichage suit l'ordre de la liste.
	 * Une même personne ne peut apparaître qu'une fois par fonction (la première ligne l'emporte).
	 */
	public void remplacerDistribution(List<MembreDistribution> membres) {
		Map<String, Participation> existantes = indexer(distribution, Participation::cle);
		List<Participation> resultat = new ArrayList<>();
		Set<String> vues = new LinkedHashSet<>();
		int ordre = 0;
		for (MembreDistribution m : membres) {
			String cle = Participation.cle(m.personne(), m.fonction());
			if (!vues.add(cle)) {
				continue;
			}
			Participation p = existantes.remove(cle);
			if (p == null) {
				p = new Participation(this, m.personne(), m.fonction(), vide(m.role()), ordre);
			} else {
				p.modifier(vide(m.role()), ordre);
			}
			resultat.add(p);
			ordre++;
		}
		distribution.removeAll(existantes.values());
		resultat.stream().filter(p -> !distribution.contains(p)).forEach(distribution::add);
		distribution.sort(Comparator.comparingInt(Participation::getOrdre));
	}

	/**
	 * Remplace les renditions vidéo (une par qualité ; la dernière ligne d'une même qualité l'emporte).
	 */
	public void remplacerVideos(Map<Qualite, VideoSaisie> parQualite) {
		Map<Qualite, FichierVideo> existantes = indexer(videos, FichierVideo::getQualite);
		videos.removeIf(v -> !parQualite.containsKey(v.getQualite()));
		parQualite.forEach((qualite, saisie) -> {
			FichierVideo video = existantes.get(qualite);
			if (video == null) {
				videos.add(new FichierVideo(this, qualite, saisie.cheminHls().trim(), saisie.tailleOctets()));
			} else {
				video.modifier(saisie.cheminHls().trim(), saisie.tailleOctets());
			}
		});
	}

	/**
	 * Définit (ou remplace) la vidéo d'une seule qualité, sans toucher aux autres.
	 */
	public void definirVideo(Qualite qualite, String chemin, Long tailleOctets) {
		Map<Qualite, VideoSaisie> parQualite = new LinkedHashMap<>();
		videos.forEach(v -> parQualite.put(v.getQualite(), new VideoSaisie(v.getCheminHls(), v.getTailleOctets())));
		parQualite.put(qualite, new VideoSaisie(chemin, tailleOctets));
		remplacerVideos(parQualite);
	}

	/**
	 * Remplace les sous-titres (un par langue, langue en minuscules).
	 */
	public void remplacerSousTitres(List<SousTitreSaisi> saisis) {
		Map<String, SousTitre> existants = indexer(sousTitres, SousTitre::getLangue);
		Map<String, SousTitreSaisi> parLangue = new LinkedHashMap<>();
		saisis.forEach(s -> parLangue.put(s.langue().trim().toLowerCase(), s));
		sousTitres.removeIf(s -> !parLangue.containsKey(s.getLangue()));
		parLangue.forEach((langue, saisi) -> {
			SousTitre st = existants.get(langue);
			if (st == null) {
				sousTitres.add(new SousTitre(this, langue, saisi.libelle().trim(), saisi.fichierVttUrl().trim()));
			} else {
				st.modifier(saisi.libelle().trim(), saisi.fichierVttUrl().trim());
			}
		});
	}

	/**
	 * Définit (ou remplace) la piste de sous-titres d'une langue, sans toucher aux autres.
	 */
	public void definirSousTitre(String langue, String libelle, String fichierVttUrl) {
		List<SousTitreSaisi> saisis = new ArrayList<>();
		sousTitres.forEach(s -> saisis.add(new SousTitreSaisi(s.getLangue(), s.getLibelle(), s.getFichierVttUrl())));
		saisis.add(new SousTitreSaisi(langue, libelle, fichierVttUrl));
		remplacerSousTitres(saisis);
	}

	/** Chemin HLS (relatif à video.cdn.base_url ou URL complète) et taille d'une rendition. */
	public record VideoSaisie(String cheminHls, Long tailleOctets) {
	}

	/** Sous-titre saisi dans le back-office. */
	public record SousTitreSaisi(String langue, String libelle, String fichierVttUrl) {
	}

	private static <K, V> Map<K, V> indexer(Collection<V> valeurs, Function<V, K> cle) {
		Map<K, V> index = new LinkedHashMap<>();
		valeurs.forEach(v -> index.put(cle.apply(v), v));
		return index;
	}

	private static String vide(String valeur) {
		return valeur == null || valeur.isBlank() ? null : valeur.trim();
	}

	@Override
	public Long getId() {
		return id;
	}

	public String getTitre() {
		return titre;
	}

	public String getSlug() {
		return slug;
	}

	public String getSynopsisCourt() {
		return synopsisCourt;
	}

	public String getSynopsis() {
		return synopsis;
	}

	public Integer getAnnee() {
		return annee;
	}

	public Integer getDureeMinutes() {
		return dureeMinutes;
	}

	public String getClassificationAge() {
		return classificationAge;
	}

	public TypeContenu getType() {
		return type;
	}

	public boolean isGratuit() {
		return gratuit;
	}

	public boolean isExclusivite() {
		return exclusivite;
	}

	public boolean isTelechargeable() {
		return telechargeable;
	}

	public String getAfficheUrl() {
		return afficheUrl;
	}

	public String getBandeauUrl() {
		return bandeauUrl;
	}

	public String getBandeAnnonceUrl() {
		return bandeAnnonceUrl;
	}

	public LocalDate getDateSortie() {
		return dateSortie;
	}

	public Set<Genre> getGenres() {
		return genres;
	}

	public Set<Pays> getPays() {
		return pays;
	}

	public StatutFilm getStatut() {
		return statut;
	}

	public List<Participation> getDistribution() {
		return Collections.unmodifiableList(distribution);
	}

	public List<FichierVideo> getVideos() {
		return Collections.unmodifiableList(videos);
	}

	public List<SousTitre> getSousTitres() {
		return Collections.unmodifiableList(sousTitres);
	}
}
