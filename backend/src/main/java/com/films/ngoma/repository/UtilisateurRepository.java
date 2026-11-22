package com.films.ngoma.repository;

import com.films.ngoma.domain.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

	Optional<Utilisateur> findByEmailIgnoreCase(String email);

	boolean existsByEmailIgnoreCase(String email);
}
