package com.films.ngoma.facade;

import com.films.ngoma.configuration.logger.Logged;
import com.films.ngoma.domain.Abonnement;
import com.films.ngoma.domain.Paiement;
import com.films.ngoma.domain.Pass;
import com.films.ngoma.domain.Utilisateur;
import com.films.ngoma.enums.MoyenPaiement;
import com.films.ngoma.enums.StatutAbonnement;
import com.films.ngoma.enums.StatutPaiement;
import com.films.ngoma.exception.PaiementException;
import com.films.ngoma.exception.PassException;
import com.films.ngoma.presentation.dto.paiement.InitierPaiementDto;
import com.films.ngoma.presentation.dto.paiement.NotificationPaiementDto;
import com.films.ngoma.presentation.dto.paiement.PaiementDto;
import com.films.ngoma.repository.AbonnementRepository;
import com.films.ngoma.repository.PaiementRepository;
import com.films.ngoma.repository.PassRepository;
import com.films.ngoma.service.SecurityService;
import com.films.ngoma.service.paiement.OperateurPaiementClient;
import com.films.ngoma.service.paiement.OperateurPaiementClient.DemandeOperateur;
import com.films.ngoma.service.paiement.OperateurPaiementClient.StatutOperateur;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;

@Service
public class PaiementFacade {

	private static final Logger log = LoggerFactory.getLogger(PaiementFacade.class);
	private static final SecureRandom ALEA = new SecureRandom();

	private final PaiementRepository paiementRepository;
	private final PassRepository passRepository;
	private final AbonnementRepository abonnementRepository;
	private final SecurityService securityService;
	private final OperateurPaiementClient operateur;
	private final long delaiExpirationMinutes;

	public PaiementFacade(PaiementRepository paiementRepository, PassRepository passRepository,
						  AbonnementRepository abonnementRepository, SecurityService securityService,
						  OperateurPaiementClient operateur,
						  @Value("${paiement.delai_expiration_minutes}") long delaiExpirationMinutes) {
		this.paiementRepository = paiementRepository;
		this.passRepository = passRepository;
		this.abonnementRepository = abonnementRepository;
		this.securityService = securityService;
		this.operateur = operateur;
		this.delaiExpirationMinutes = delaiExpirationMinutes;
	}

	/**
	 * Crée un paiement EN_ATTENTE pour le pass choisi et le transmet à l'opérateur. L'utilisateur valide
	 * ensuite sur son téléphone (ou sur la page de l'opérateur si {@code urlPaiement} est renseignée).
	 */
	@Transactional
	@Logged
	public PaiementDto initier(InitierPaiementDto dto) {
		Utilisateur utilisateur = securityService.utilisateurConnecte();
		Pass pass = passRepository.findByCodeAndActifTrue(dto.codePass())
				.orElseThrow(() -> PassException.passInconnu(dto.codePass()));
		if (dto.moyen() != MoyenPaiement.CARTE && (dto.telephone() == null || dto.telephone().isBlank())) {
			throw PaiementException.telephoneObligatoire();
		}
		Paiement paiement = paiementRepository.saveAndFlush(
				new Paiement(genererReference(), utilisateur, pass, dto.moyen(), dto.telephone()));
		DemandeOperateur demande = operateur.initier(paiement);
		paiement.transmettre(demande.referenceOperateur());
		log.info("Paiement {} initié : {} FCFA via {} ({})", paiement.getReference(), paiement.getMontantFcfa(),
				paiement.getMoyen(), operateur.nom());
		return new PaiementDto(paiement, demande.urlPaiement(), operateur.modeTest());
	}

	/**
	 * État d'un paiement de l'utilisateur connecté (écran d'attente). Tant qu'il est en attente,
	 * l'état est vérifié auprès de l'opérateur à chaque consultation.
	 */
	@Transactional
	public PaiementDto consulter(String reference) {
		Long utilisateurId = securityService.utilisateurConnecte().getId();
		Paiement paiement = paiementRepository.findByReferenceAndUtilisateurId(reference, utilisateurId)
				.orElseThrow(() -> PaiementException.paiementInconnu(reference));
		synchroniser(paiement);
		return new PaiementDto(paiement, null, operateur.modeTest());
	}

	/**
	 * Notification (webhook) de l'opérateur. Son contenu n'est jamais cru sur parole : après contrôle
	 * d'authenticité, l'état réel est redemandé à l'opérateur. Idempotent.
	 */
	@Transactional
	@Logged
	public void traiterNotification(String signature, NotificationPaiementDto notification) {
		if (!operateur.notificationAuthentique(signature, notification)) {
			throw PaiementException.signatureInvalide();
		}
		Paiement paiement = paiementRepository.findByReference(notification.reference())
				.orElseThrow(() -> PaiementException.paiementInconnu(notification.reference()));
		if (!paiement.estEnAttente()) {
			log.info("Notification ignorée : paiement {} déjà au statut {}", paiement.getReference(), paiement.getStatut());
			return;
		}
		synchroniser(paiement);
	}

	/**
	 * Vérification de secours (tâche planifiée) : interroge l'opérateur pour les paiements encore en attente,
	 * au cas où une notification se serait perdue.
	 *
	 * @return le nombre de paiements dont l'état a changé
	 */
	@Transactional
	public int verifierPaiementsEnAttente() {
		LocalDateTime maintenant = LocalDateTime.now();
		int changes = 0;
		for (Paiement paiement : paiementRepository.findByStatutAndCreateAtAfter(StatutPaiement.EN_ATTENTE,
				maintenant.minusMinutes(delaiExpirationMinutes))) {
			if (synchroniser(paiement)) {
				changes++;
			}
		}
		return changes;
	}

	/**
	 * Aligne le paiement sur l'état de l'opérateur ; à la confirmation, active ou prolonge le pass.
	 *
	 * @return true si l'état du paiement a changé
	 */
	boolean synchroniser(Paiement paiement) {
		if (!paiement.estEnAttente()) {
			return false;
		}
		StatutOperateur statut = operateur.consulter(paiement);
		switch (statut.etat()) {
			case CONFIRME -> {
				paiement.confirmer(statut.referenceOperateur(), activerPass(paiement));
				log.info("Paiement {} confirmé ({})", paiement.getReference(), operateur.nom());
				return true;
			}
			case ECHOUE -> {
				paiement.echouer(statut.referenceOperateur());
				log.info("Paiement {} refusé ({})", paiement.getReference(), operateur.nom());
				return true;
			}
			default -> {
				return false;
			}
		}
	}

	/** Un nouveau pass prolonge l'abonnement en cours au lieu de se chevaucher avec lui. */
	private Abonnement activerPass(Paiement paiement) {
		LocalDateTime maintenant = LocalDateTime.now();
		LocalDateTime debut = abonnementRepository
				.findFirstByUtilisateurIdAndStatutAndDateFinAfterOrderByDateFinDesc(
						paiement.getUtilisateur().getId(), StatutAbonnement.ACTIF, maintenant)
				.map(Abonnement::getDateFin)
				.orElse(maintenant);
		return abonnementRepository.save(new Abonnement(paiement.getUtilisateur(), paiement.getPass(), debut));
	}

	private static String genererReference() {
		byte[] octets = new byte[4];
		ALEA.nextBytes(octets);
		return "NG" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
				+ HexFormat.of().withUpperCase().formatHex(octets);
	}
}
