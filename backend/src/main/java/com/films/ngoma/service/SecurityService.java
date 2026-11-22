package com.films.ngoma.service;

import com.films.ngoma.domain.Utilisateur;
import com.films.ngoma.exception.UtilisateurException;
import com.films.ngoma.presentation.dto.auth.AuthDto;
import com.films.ngoma.presentation.dto.auth.InscriptionDto;
import com.films.ngoma.presentation.dto.auth.TokenDto;
import com.films.ngoma.repository.UtilisateurRepository;
import com.films.ngoma.security.JwtTokenUtils;
import com.films.ngoma.security.SecurityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SecurityService implements UserDetailsService {

	private final UtilisateurRepository utilisateurRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenUtils jwtTokenUtils;

	public SecurityService(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder,
						   JwtTokenUtils jwtTokenUtils) {
		this.utilisateurRepository = utilisateurRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtTokenUtils = jwtTokenUtils;
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String email) {
		Utilisateur utilisateur = utilisateurRepository.findByEmailIgnoreCase(email)
				.orElseThrow(() -> new UsernameNotFoundException(email));
		return User.withUsername(utilisateur.getEmail())
				.password(utilisateur.getMotDePasseHash())
				.roles(utilisateur.getRole().name())
				.build();
	}

	/**
	 * Authentifie l'utilisateur et renvoie son jeton.
	 */
	@Transactional(readOnly = true)
	public TokenDto authentifier(AuthDto authDto) {
		Utilisateur utilisateur = utilisateurRepository.findByEmailIgnoreCase(authDto.email().trim())
				.filter(u -> passwordEncoder.matches(authDto.motDePasse(), u.getMotDePasseHash()))
				.orElseThrow(UtilisateurException::identifiantsIncorrects);
		return new TokenDto(jwtTokenUtils.generateToken(utilisateur));
	}

	/**
	 * Crée un compte abonné et le connecte directement.
	 */
	@Transactional
	public TokenDto inscrire(InscriptionDto dto) {
		if (utilisateurRepository.existsByEmailIgnoreCase(dto.email().trim())) {
			throw UtilisateurException.emailDejaUtilise(dto.email());
		}
		Utilisateur utilisateur = utilisateurRepository.save(new Utilisateur(dto.nom().trim(), dto.email(),
				dto.telephone(), passwordEncoder.encode(dto.motDePasse())));
		return new TokenDto(jwtTokenUtils.generateToken(utilisateur));
	}

	/**
	 * @return l'utilisateur connecté (entité).
	 */
	@Transactional(readOnly = true)
	public Utilisateur utilisateurConnecte() {
		return utilisateurRepository.findByEmailIgnoreCase(SecurityUtils.lireLoginUtilisateurConnecte())
				.orElseThrow(UtilisateurException::sessionExpiree);
	}

}
