package com.films.ngoma.facade;

import com.films.ngoma.domain.Utilisateur;
import com.films.ngoma.domain.UtilisateurPhoto;
import com.films.ngoma.exception.UtilisateurException;
import com.films.ngoma.presentation.dto.UtilisateurDto;
import com.films.ngoma.presentation.dto.compte.ChangementMotDePasseDto;
import com.films.ngoma.presentation.dto.compte.ModificationProfilDto;
import com.films.ngoma.repository.UtilisateurPhotoRepository;
import com.films.ngoma.service.SecurityService;
import com.films.ngoma.utils.ImageUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Gestion du compte de l'utilisateur connecté (« Mon compte »).
 */
@Service
public class UtilisateurFacade {

	/** Taille maximale d'une photo de profil : 2 Mo. */
	public static final long TAILLE_MAX_PHOTO = 2L * 1024 * 1024;

	private final SecurityService securityService;
	private final UtilisateurPhotoRepository photoRepository;
	private final PasswordEncoder passwordEncoder;

	public UtilisateurFacade(SecurityService securityService, UtilisateurPhotoRepository photoRepository,
							 PasswordEncoder passwordEncoder) {
		this.securityService = securityService;
		this.photoRepository = photoRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional(readOnly = true)
	public UtilisateurDto profil() {
		return versDto(securityService.utilisateurConnecte());
	}

	@Transactional
	public UtilisateurDto modifierProfil(ModificationProfilDto dto) {
		Utilisateur utilisateur = securityService.utilisateurConnecte();
		String telephone = dto.telephone() == null || dto.telephone().isBlank() ? null : dto.telephone().replace(" ", "");
		utilisateur.modifierProfil(dto.nom().trim(), telephone);
		return versDto(utilisateur);
	}

	@Transactional
	public void changerMotDePasse(ChangementMotDePasseDto dto) {
		Utilisateur utilisateur = securityService.utilisateurConnecte();
		if (!passwordEncoder.matches(dto.motDePasseActuel(), utilisateur.getMotDePasseHash())) {
			throw UtilisateurException.motDePasseActuelIncorrect();
		}
		utilisateur.changerMotDePasse(passwordEncoder.encode(dto.nouveauMotDePasse()));
	}

	@Transactional(readOnly = true)
	public UtilisateurPhoto photo() {
		return photoRepository.findById(securityService.utilisateurConnecte().getId())
				.orElseThrow(UtilisateurException::aucunePhoto);
	}

	/**
	 * Enregistre (ou remplace) la photo de profil. Le format est vérifié sur le contenu du fichier,
	 * pas sur le type annoncé par le navigateur.
	 */
	@Transactional
	public UtilisateurDto enregistrerPhoto(MultipartFile fichier) {
		if (fichier == null || fichier.isEmpty() || fichier.getSize() > TAILLE_MAX_PHOTO) {
			throw UtilisateurException.photoInvalide();
		}
		byte[] contenu;
		try {
			contenu = fichier.getBytes();
		} catch (IOException e) {
			throw UtilisateurException.photoInvalide();
		}
		String type = ImageUtils.detecterType(contenu);
		if (type == null) {
			throw UtilisateurException.photoInvalide();
		}
		Utilisateur utilisateur = securityService.utilisateurConnecte();
		UtilisateurPhoto photo = photoRepository.findById(utilisateur.getId())
				.orElseGet(() -> new UtilisateurPhoto(utilisateur.getId()));
		photo.remplacer(contenu, type);
		photoRepository.save(photo);
		return new UtilisateurDto(utilisateur, true);
	}

	@Transactional
	public UtilisateurDto supprimerPhoto() {
		Utilisateur utilisateur = securityService.utilisateurConnecte();
		photoRepository.deleteById(utilisateur.getId());
		return new UtilisateurDto(utilisateur, false);
	}

	private UtilisateurDto versDto(Utilisateur utilisateur) {
		return new UtilisateurDto(utilisateur, photoRepository.existsById(utilisateur.getId()));
	}

}
