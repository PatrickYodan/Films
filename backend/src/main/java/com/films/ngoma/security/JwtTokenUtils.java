package com.films.ngoma.security;

import com.films.ngoma.domain.Utilisateur;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtTokenUtils {

	private final SecretKey cle;
	private final long expiration;

	public JwtTokenUtils(@Value("${application.security.jwt.secret-key}") String secret,
						 @Value("${application.security.jwt.expiration}") long expiration) {
		this.cle = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
		this.expiration = expiration;
	}

	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> resolver) {
		return resolver.apply(Jwts.parser().verifyWith(cle).build().parseSignedClaims(token).getPayload());
	}

	public boolean isTokenExpired(String token) {
		return getExpirationDateFromToken(token).before(new Date());
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		return getUsernameFromToken(token).equals(userDetails.getUsername()) && !isTokenExpired(token);
	}

	/**
	 * Génère le jeton JWT de l'utilisateur (sujet = e-mail).
	 */
	public String generateToken(Utilisateur utilisateur) {
		long maintenant = System.currentTimeMillis();
		return Jwts.builder()
				.subject(utilisateur.getEmail())
				.claim("id", utilisateur.getId())
				.claim("nom", utilisateur.getNom())
				.claim("role", utilisateur.getRole().name())
				.issuedAt(new Date(maintenant))
				.expiration(new Date(maintenant + expiration))
				.signWith(cle)
				.compact();
	}
}
