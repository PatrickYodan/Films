package com.films.ngoma.controller;

import com.films.ngoma.configuration.logger.Logged;
import com.films.ngoma.facade.PaiementFacade;
import com.films.ngoma.presentation.dto.paiement.InitierPaiementDto;
import com.films.ngoma.presentation.dto.paiement.NotificationPaiementDto;
import com.films.ngoma.presentation.dto.paiement.PaiementDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/paiement")
public class PaiementController {

	private final PaiementFacade paiementFacade;

	public PaiementController(PaiementFacade paiementFacade) {
		this.paiementFacade = paiementFacade;
	}

	/**
	 * Lance le paiement d'un pass.
	 */
	@PostMapping("/initier")
	@ResponseStatus(HttpStatus.CREATED)
	@SecurityRequirement(name = "Authorization")
	@Logged
	public PaiementDto initier(@Valid @RequestBody InitierPaiementDto dto) {
		return paiementFacade.initier(dto);
	}

	/**
	 * Suivi d'un paiement (interrogé par l'écran d'attente de confirmation).
	 */
	@GetMapping("/{reference}")
	@SecurityRequirement(name = "Authorization")
	public PaiementDto consulter(@PathVariable String reference) {
		return paiementFacade.consulter(reference);
	}

	/**
	 * Notification de l'opérateur de paiement (webhook).
	 */
	@PostMapping("/notification")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void notification(@RequestHeader(name = "X-Ngoma-Signature", required = false) String signature,
							 @RequestBody NotificationPaiementDto notification) {
		paiementFacade.traiterNotification(signature, notification);
	}
}
