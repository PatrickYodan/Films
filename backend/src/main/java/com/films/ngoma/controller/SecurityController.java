package com.films.ngoma.controller;

import com.films.ngoma.configuration.logger.Logged;
import com.films.ngoma.presentation.dto.auth.AuthDto;
import com.films.ngoma.presentation.dto.auth.InscriptionDto;
import com.films.ngoma.presentation.dto.auth.TokenDto;
import com.films.ngoma.service.SecurityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/securite")
public class SecurityController {

	private final SecurityService securityService;

	public SecurityController(SecurityService securityService) {
		this.securityService = securityService;
	}

	/**
	 * Authentifie l'utilisateur.
	 *
	 * @return le jeton d'authentification
	 */
	@PostMapping("/auth")
	@Logged
	public TokenDto authentification(@Valid @RequestBody AuthDto authDto) {
		return securityService.authentifier(authDto);
	}

	/**
	 * Crée un compte abonné.
	 *
	 * @return le jeton d'authentification du nouveau compte
	 */
	@PostMapping("/inscription")
	@ResponseStatus(HttpStatus.CREATED)
	@Logged
	public TokenDto inscription(@Valid @RequestBody InscriptionDto inscriptionDto) {
		return securityService.inscrire(inscriptionDto);
	}
}
