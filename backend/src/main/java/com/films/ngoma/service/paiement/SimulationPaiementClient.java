package com.films.ngoma.service.paiement;

import com.films.ngoma.domain.Paiement;
import com.films.ngoma.presentation.dto.paiement.NotificationPaiementDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Opérateur de paiement simulé, pour développer et tester sans compte marchand.
 * <ul>
 *   <li>le paiement est confirmé automatiquement après {@code paiement.simulation.delai_secondes} ;</li>
 *   <li>un numéro de téléphone se terminant par « 00 » simule un refus.</li>
 * </ul>
 * Jamais actif avec le profil « prod ».
 */
@Service
@Profile("!prod")
@ConditionalOnProperty(name = "paiement.operateur.fournisseur", havingValue = "simulation")
public class SimulationPaiementClient implements OperateurPaiementClient {

	private static final Logger log = LoggerFactory.getLogger(SimulationPaiementClient.class);
	static final String SUFFIXE_REFUS = "00";

	private final Duration delai;
	private final String secretNotification;

	public SimulationPaiementClient(@Value("${paiement.simulation.delai_secondes:8}") long delaiSecondes,
									@Value("${paiement.operateur.secret_webhook:}") String secretNotification) {
		this.delai = Duration.ofSeconds(delaiSecondes);
		this.secretNotification = secretNotification;
		log.warn("Paiements en MODE SIMULATION : aucun argent réel ne circule (confirmation après {} s).", delaiSecondes);
	}

	@Override
	public String nom() {
		return "simulation";
	}

	@Override
	public boolean modeTest() {
		return true;
	}

	@Override
	public DemandeOperateur initier(Paiement paiement) {
		log.info("[simulation] Demande de {} FCFA envoyée ({})", paiement.getMontantFcfa(), paiement.getMoyen());
		return new DemandeOperateur("SIM-" + paiement.getReference(), null);
	}

	@Override
	public StatutOperateur consulter(Paiement paiement) {
		String referenceOperateur = "SIM-" + paiement.getReference();
		String telephone = paiement.getTelephone();
		if (telephone != null && telephone.endsWith(SUFFIXE_REFUS)) {
			return new StatutOperateur(Etat.ECHOUE, referenceOperateur);
		}
		LocalDateTime cree = paiement.getCreateAt() == null ? LocalDateTime.now() : paiement.getCreateAt();
		boolean delaiEcoule = !LocalDateTime.now().isBefore(cree.plus(delai));
		return new StatutOperateur(delaiEcoule ? Etat.CONFIRME : Etat.EN_ATTENTE, referenceOperateur);
	}

	/** En simulation, la notification manuelle est protégée par le secret partagé NGOMA_PAIEMENT_SECRET. */
	@Override
	public boolean notificationAuthentique(String signature, NotificationPaiementDto notification) {
		return secretNotification != null && !secretNotification.isBlank() && signature != null
				&& MessageDigest.isEqual(secretNotification.getBytes(StandardCharsets.UTF_8),
				signature.getBytes(StandardCharsets.UTF_8));
	}
}
