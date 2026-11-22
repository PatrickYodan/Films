package com.films.ngoma.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public final class SecurityUtils {

	/** Login par défaut lorsqu'aucun utilisateur n'est connecté. */
	public static final String DEFAULT_LOGIN = "ANONYMOUS";

	private SecurityUtils() {
	}

	/**
	 * @return le login (e-mail) de l'utilisateur connecté, ou {@link #DEFAULT_LOGIN}.
	 */
	public static String lireLoginUtilisateurConnecte() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			return DEFAULT_LOGIN;
		}
		Object principal = authentication.getPrincipal();
		if (principal instanceof UserDetails userDetails) {
			return userDetails.getUsername();
		}
		return principal instanceof String login ? login : DEFAULT_LOGIN;
	}
}
