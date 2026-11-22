package com.films.ngoma.repository;

import com.films.ngoma.domain.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {

	Optional<Paiement> findByReference(String reference);

	/** Paiements d'un statut créés après une date (vérification de secours). */
	java.util.List<Paiement> findByStatutAndCreateAtAfter(com.films.ngoma.enums.StatutPaiement statut, LocalDateTime apres);

	Optional<Paiement> findByReferenceAndUtilisateurId(String reference, Long utilisateurId);

	@Modifying
	@Query("update Paiement p set p.statut = com.films.ngoma.enums.StatutPaiement.EXPIRE "
			+ "where p.statut = com.films.ngoma.enums.StatutPaiement.EN_ATTENTE and p.createAt <= :limite")
	int expirerPaiementsEnAttente(@Param("limite") LocalDateTime limite);
}
