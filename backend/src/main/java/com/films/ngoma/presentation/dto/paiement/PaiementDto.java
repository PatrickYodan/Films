package com.films.ngoma.presentation.dto.paiement;

import com.films.ngoma.domain.Paiement;
import com.films.ngoma.enums.MoyenPaiement;
import com.films.ngoma.enums.StatutPaiement;

import java.time.LocalDateTime;

/**
 * État d'un paiement.
 *
 * @param urlPaiement page de l'opérateur où rediriger l'utilisateur (carte, Wave…), sinon null
 * @param modeTest    true si l'opérateur est simulé : aucun argent réel ne circule
 */
public record PaiementDto(String reference, String codePass, MoyenPaiement moyen, String telephone,
						  int montantFcfa, StatutPaiement statut, LocalDateTime creeLe, LocalDateTime confirmeLe,
						  String urlPaiement, boolean modeTest) {

	public PaiementDto(Paiement p, String urlPaiement, boolean modeTest) {
		this(p.getReference(), p.getPass().getCode(), p.getMoyen(), p.getTelephone(),
				p.getMontantFcfa(), p.getStatut(), p.getCreateAt(), p.getConfirmeLe(), urlPaiement, modeTest);
	}
}
