package com.films.ngoma.presentation.dto.abonnement;

import com.films.ngoma.domain.Abonnement;
import com.films.ngoma.enums.StatutAbonnement;
import com.films.ngoma.presentation.dto.PassDto;

import java.time.LocalDateTime;

public record AbonnementDto(Long id, PassDto pass, LocalDateTime dateDebut, LocalDateTime dateFin, StatutAbonnement statut) {

	public AbonnementDto(Abonnement a) {
		this(a.getId(), new PassDto(a.getPass()), a.getDateDebut(), a.getDateFin(), a.getStatut());
	}
}
