package com.films.ngoma.service.paiement;

import com.films.ngoma.domain.Paiement;
import com.films.ngoma.presentation.dto.paiement.NotificationPaiementDto;

/**
 * Passerelle vers l'opérateur (ou l'agrégateur) de paiement. Une seule implémentation est active,
 * choisie par la propriété {@code paiement.operateur.fournisseur} :
 * <ul>
 *   <li>{@code simulation} : développement uniquement, aucun argent ne circule ({@link SimulationPaiementClient}) ;</li>
 *   <li>un agrégateur réel (CinetPay, Paystack, PayDunya…) : à ajouter quand un compte marchand sera ouvert.</li>
 * </ul>
 * Sans implémentation active, l'application refuse de démarrer : aucun paiement ne peut être pris par erreur.
 */
public interface OperateurPaiementClient {

	/** Nom du fournisseur (journaux). */
	String nom();

	/** true si aucun argent réel ne circule (affiché à l'utilisateur). */
	boolean modeTest();

	/**
	 * Transmet la demande de paiement à l'opérateur (envoi USSD / push sur le téléphone, ou page de paiement).
	 */
	DemandeOperateur initier(Paiement paiement);

	/**
	 * Interroge l'opérateur sur l'état réel du paiement. C'est la seule source de vérité :
	 * une notification ne fait que déclencher cette vérification.
	 */
	StatutOperateur consulter(Paiement paiement);

	/** Vérifie que la notification (webhook) vient bien de l'opérateur (signature, jeton…). */
	boolean notificationAuthentique(String signature, NotificationPaiementDto notification);

	/**
	 * @param referenceOperateur identifiant de la transaction chez l'opérateur
	 * @param urlPaiement        page où rediriger l'utilisateur (carte, Wave…), ou null si la validation se fait sur le téléphone
	 */
	record DemandeOperateur(String referenceOperateur, String urlPaiement) {
	}

	record StatutOperateur(Etat etat, String referenceOperateur) {
	}

	enum Etat {
		EN_ATTENTE,
		CONFIRME,
		ECHOUE
	}
}
