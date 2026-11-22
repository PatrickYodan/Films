package com.films.ngoma.controller;

import com.films.ngoma.configuration.logger.Logged;
import com.films.ngoma.enums.Qualite;
import com.films.ngoma.enums.StatutFilm;
import com.films.ngoma.facade.AdminFilmFacade;
import com.films.ngoma.presentation.dto.PageDto;
import com.films.ngoma.presentation.dto.admin.FichierVideoDto;
import com.films.ngoma.presentation.dto.admin.FilmAdminDetailDto;
import com.films.ngoma.presentation.dto.admin.FilmAdminResumeDto;
import com.films.ngoma.presentation.dto.admin.FilmEditionDto;
import com.films.ngoma.presentation.dto.admin.ParticipationDto;
import com.films.ngoma.presentation.dto.admin.SousTitreDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * Back-office : gestion des films. Réservé au rôle ADMIN (ici et dans SecurityConfig).
 */
@RestController
@RequestMapping("${api.prefix}/admin/film")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "Authorization")
public class AdminFilmController {

	private static final int TAILLE_MAX = 100;

	private final AdminFilmFacade adminFilmFacade;

	public AdminFilmController(AdminFilmFacade adminFilmFacade) {
		this.adminFilmFacade = adminFilmFacade;
	}

	@GetMapping("/lister")
	public PageDto<FilmAdminResumeDto> lister(@RequestParam(required = false) String motCle,
											  @RequestParam(required = false) StatutFilm statut,
											  @RequestParam(defaultValue = "0") int page,
											  @RequestParam(defaultValue = "20") int taille) {
		return adminFilmFacade.lister(motCle, statut,
				PageRequest.of(Math.max(page, 0), Math.min(Math.max(taille, 1), TAILLE_MAX)));
	}

	@GetMapping("/{id}")
	public FilmAdminDetailDto detail(@PathVariable Long id) {
		return adminFilmFacade.detail(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Logged
	public FilmAdminDetailDto creer(@Valid @RequestBody FilmEditionDto dto) {
		return adminFilmFacade.creer(dto);
	}

	@PutMapping("/{id}")
	@Logged
	public FilmAdminDetailDto modifier(@PathVariable Long id, @Valid @RequestBody FilmEditionDto dto) {
		return adminFilmFacade.modifier(id, dto);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Logged
	public void supprimer(@PathVariable Long id) {
		adminFilmFacade.supprimer(id);
	}

	@PostMapping("/{id}/publier")
	@Logged
	public FilmAdminDetailDto publier(@PathVariable Long id) {
		return adminFilmFacade.publier(id);
	}

	@PostMapping("/{id}/depublier")
	@Logged
	public FilmAdminDetailDto depublier(@PathVariable Long id) {
		return adminFilmFacade.depublier(id);
	}

	@PostMapping(path = "/{id}/affiche", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Logged
	public FilmAdminDetailDto envoyerAffiche(@PathVariable Long id, @RequestParam("fichier") MultipartFile fichier) {
		return adminFilmFacade.changerAffiche(id, fichier);
	}

	@PostMapping(path = "/{id}/bandeau", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Logged
	public FilmAdminDetailDto envoyerBandeau(@PathVariable Long id, @RequestParam("fichier") MultipartFile fichier) {
		return adminFilmFacade.changerBandeau(id, fichier);
	}

	@PutMapping("/{id}/distribution")
	@Logged
	public FilmAdminDetailDto distribution(@PathVariable Long id,
										   @RequestBody @Size(max = 100) List<@Valid ParticipationDto> lignes) {
		return adminFilmFacade.remplacerDistribution(id, lignes);
	}

	@PutMapping("/{id}/videos")
	@Logged
	public FilmAdminDetailDto videos(@PathVariable Long id,
									 @RequestBody @Size(max = 3) List<@Valid FichierVideoDto> lignes) {
		return adminFilmFacade.remplacerVideos(id, lignes);
	}

	/**
	 * Envoi d'un fichier vidéo MP4 pour une qualité : corps brut (application/octet-stream), lu en flux.
	 * Taille maximale : ngoma.video.taille_max (4 Go par défaut).
	 */
	@PutMapping(path = "/{id}/videos/{qualite}/fichier", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@Logged
	public FilmAdminDetailDto envoyerVideo(@PathVariable Long id, @PathVariable Qualite qualite, InputStream flux,
										   @RequestHeader(name = "Content-Length", required = false) Long taille) {
		return adminFilmFacade.envoyerVideo(id, qualite, flux, taille);
	}

	/**
	 * Envoi d'un fichier de sous-titres (.vtt ou .srt, 1 Mo maximum) pour une langue.
	 */
	@PostMapping(path = "/{id}/sous-titres/fichier", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Logged
	public FilmAdminDetailDto envoyerSousTitre(@PathVariable Long id, @RequestParam String langue,
											   @RequestParam String libelle, @RequestParam("fichier") MultipartFile fichier) {
		return adminFilmFacade.envoyerSousTitre(id, langue, libelle, fichier);
	}

	@PutMapping("/{id}/sous-titres")
	@Logged
	public FilmAdminDetailDto sousTitres(@PathVariable Long id,
										 @RequestBody @Size(max = 20) List<@Valid SousTitreDto> lignes) {
		return adminFilmFacade.remplacerSousTitres(id, lignes);
	}
}
