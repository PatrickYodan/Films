package com.films.ngoma.controller;

import com.films.ngoma.configuration.logger.Logged;
import com.films.ngoma.domain.UtilisateurPhoto;
import com.films.ngoma.facade.UtilisateurFacade;
import com.films.ngoma.presentation.dto.UtilisateurDto;
import com.films.ngoma.presentation.dto.compte.ChangementMotDePasseDto;
import com.films.ngoma.presentation.dto.compte.ModificationProfilDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * « Mon compte » : profil, mot de passe et photo de l'utilisateur connecté.
 */
@RestController
@RequestMapping("${api.prefix}/utilisateur")
@SecurityRequirement(name = "Authorization")
public class UtilisateurController {

	private final UtilisateurFacade utilisateurFacade;

	public UtilisateurController(UtilisateurFacade utilisateurFacade) {
		this.utilisateurFacade = utilisateurFacade;
	}

	@GetMapping("/moi")
	public UtilisateurDto moi() {
		return utilisateurFacade.profil();
	}

	@PutMapping("/moi")
	@Logged
	public UtilisateurDto modifier(@Valid @RequestBody ModificationProfilDto dto) {
		return utilisateurFacade.modifierProfil(dto);
	}

	@PutMapping("/moi/mot-de-passe")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Logged
	public void changerMotDePasse(@Valid @RequestBody ChangementMotDePasseDto dto) {
		utilisateurFacade.changerMotDePasse(dto);
	}

	@GetMapping("/moi/photo")
	public ResponseEntity<byte[]> photo() {
		UtilisateurPhoto photo = utilisateurFacade.photo();
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(photo.getTypeMime()))
				.cacheControl(CacheControl.noCache().cachePrivate())
				.body(photo.getContenu());
	}

	@PostMapping(path = "/moi/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Logged
	public UtilisateurDto envoyerPhoto(@RequestParam("fichier") MultipartFile fichier) {
		return utilisateurFacade.enregistrerPhoto(fichier);
	}

	@DeleteMapping("/moi/photo")
	@Logged
	public UtilisateurDto supprimerPhoto() {
		return utilisateurFacade.supprimerPhoto();
	}
}
