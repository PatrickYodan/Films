package com.films.ngoma.presentation.dto;

import com.films.ngoma.domain.Utilisateur;
import com.films.ngoma.enums.Role;

import java.time.LocalDateTime;

/**
 * Profil de l'utilisateur. {@code photo} indique s'il a une photo de profil.
 */
public record UtilisateurDto(Long id, String nom, String email, String telephone, Role role,
							 boolean photo, LocalDateTime inscritLe) {

	public UtilisateurDto(Utilisateur u, boolean photo) {
		this(u.getId(), u.getNom(), u.getEmail(), u.getTelephone(), u.getRole(), photo, u.getCreateAt());
	}
}
