package com.films.ngoma.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	private final UserDetailsService userDetailsService;
	private final JwtTokenUtils jwtTokenUtils;
	private final String cheminAuthentification;

	public JwtRequestFilter(UserDetailsService userDetailsService, JwtTokenUtils jwtTokenUtils,
							@Value("${api.prefix}") String apiPrefix) {
		this.userDetailsService = userDetailsService;
		this.jwtTokenUtils = jwtTokenUtils;
		this.cheminAuthentification = apiPrefix + "/securite/";
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		final String authHeader = request.getHeader("Authorization");
		if (request.getServletPath().startsWith(cheminAuthentification)
				|| authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		final String jwt = authHeader.substring(7);
		try {
			final String username = jwtTokenUtils.getUsernameFromToken(jwt);
			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				if (jwtTokenUtils.isTokenValid(jwt, userDetails)) {
					UsernamePasswordAuthenticationToken authToken =
							new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
			}
		} catch (Exception e) {
			// Jeton expiré, malformé ou signature invalide : on continue en anonyme,
			// Spring Security renverra 401 sur les endpoints protégés.
			SecurityContextHolder.clearContext();
		}
		filterChain.doFilter(request, response);
	}
}
