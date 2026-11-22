package com.films.ngoma.task;

import com.films.ngoma.repository.AbonnementRepository;
import com.films.ngoma.repository.PaiementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Passe en EXPIRE les abonnements échus et les paiements restés trop longtemps en attente.
 */
@Component
public class ExpirationAbonnementTask {

	private static final Logger log = LoggerFactory.getLogger(ExpirationAbonnementTask.class);

	private final AbonnementRepository abonnementRepository;
	private final PaiementRepository paiementRepository;
	private final long delaiPaiementMinutes;

	public ExpirationAbonnementTask(AbonnementRepository abonnementRepository, PaiementRepository paiementRepository,
									@Value("${paiement.delai_expiration_minutes}") long delaiPaiementMinutes) {
		this.abonnementRepository = abonnementRepository;
		this.paiementRepository = paiementRepository;
		this.delaiPaiementMinutes = delaiPaiementMinutes;
	}

	@Scheduled(fixedDelayString = "PT5M", initialDelayString = "PT1M")
	@Transactional
	public void expirer() {
		LocalDateTime maintenant = LocalDateTime.now();
		int abonnements = abonnementRepository.expirerAbonnementsEchus(maintenant);
		int paiements = paiementRepository.expirerPaiementsEnAttente(maintenant.minusMinutes(delaiPaiementMinutes));
		if (abonnements + paiements > 0) {
			log.info("Expiration : {} abonnement(s), {} paiement(s)", abonnements, paiements);
		}
	}
}
