package com.films.ngoma.repository;

import com.films.ngoma.domain.Abonnement;
import com.films.ngoma.enums.StatutAbonnement;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AbonnementRepository extends JpaRepository<Abonnement, Long> {

	@EntityGraph(attributePaths = "pass")
	Optional<Abonnement> findFirstByUtilisateurIdAndStatutAndDateFinAfterOrderByDateFinDesc(
			Long utilisateurId, StatutAbonnement statut, LocalDateTime maintenant);

	@Modifying
	@Query("update Abonnement a set a.statut = com.films.ngoma.enums.StatutAbonnement.EXPIRE "
			+ "where a.statut = com.films.ngoma.enums.StatutAbonnement.ACTIF and a.dateFin <= :maintenant")
	int expirerAbonnementsEchus(@Param("maintenant") LocalDateTime maintenant);
}
