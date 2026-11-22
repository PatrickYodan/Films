package com.films.ngoma.controller;

import com.films.ngoma.facade.AbonnementFacade;
import com.films.ngoma.presentation.dto.abonnement.AbonnementDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/abonnement")
@SecurityRequirement(name = "Authorization")
public class AbonnementController {

	private final AbonnementFacade abonnementFacade;

	public AbonnementController(AbonnementFacade abonnementFacade) {
		this.abonnementFacade = abonnementFacade;
	}

	/**
	 * @return l'abonnement actif (200) ou 204 si l'utilisateur n'en a pas
	 */
	@GetMapping("/courant")
	public ResponseEntity<AbonnementDto> courant() {
		return abonnementFacade.courant()
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.noContent().build());
	}
}
