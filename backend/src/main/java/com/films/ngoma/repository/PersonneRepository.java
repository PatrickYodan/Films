package com.films.ngoma.repository;

import com.films.ngoma.domain.Personne;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonneRepository extends JpaRepository<Personne, Long> {

	/** Recherche par nom sans tenir compte de la casse (les espaces sont normalisés à la création). */
	Optional<Personne> findFirstByNomIgnoreCaseOrderByIdAsc(String nom);
}
