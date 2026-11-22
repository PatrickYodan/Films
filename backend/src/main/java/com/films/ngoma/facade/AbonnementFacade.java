package com.films.ngoma.facade;

import com.films.ngoma.enums.StatutAbonnement;
import com.films.ngoma.presentation.dto.abonnement.AbonnementDto;
import com.films.ngoma.repository.AbonnementRepository;
import com.films.ngoma.service.SecurityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AbonnementFacade {

	private final AbonnementRepository abonnementRepository;
	private final SecurityService securityService;

	public AbonnementFacade(AbonnementRepository abonnementRepository, SecurityService securityService) {
		this.abonnementRepository = abonnementRepository;
		this.securityService = securityService;
	}

	/**
	 * @return l'abonnement actif de l'utilisateur connecté, s'il en a un.
	 */
	@Transactional(readOnly = true)
	public Optional<AbonnementDto> courant() {
		Long utilisateurId = securityService.utilisateurConnecte().getId();
		return abonnementRepository
				.findFirstByUtilisateurIdAndStatutAndDateFinAfterOrderByDateFinDesc(
						utilisateurId, StatutAbonnement.ACTIF, LocalDateTime.now())
				.map(AbonnementDto::new);
	}
}
