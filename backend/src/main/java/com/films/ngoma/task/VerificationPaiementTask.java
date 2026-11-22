package com.films.ngoma.task;

import com.films.ngoma.facade.PaiementFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Vérification de secours des paiements en attente auprès de l'opérateur
 * (si une notification n'arrive jamais, le pass est quand même activé).
 */
@Component
public class VerificationPaiementTask {

	private static final Logger log = LoggerFactory.getLogger(VerificationPaiementTask.class);

	private final PaiementFacade paiementFacade;

	public VerificationPaiementTask(PaiementFacade paiementFacade) {
		this.paiementFacade = paiementFacade;
	}

	@Scheduled(fixedDelayString = "${paiement.verification.intervalle:PT30S}", initialDelayString = "PT30S")
	public void verifier() {
		int changes = paiementFacade.verifierPaiementsEnAttente();
		if (changes > 0) {
			log.info("Vérification des paiements : {} paiement(s) mis à jour", changes);
		}
	}
}
