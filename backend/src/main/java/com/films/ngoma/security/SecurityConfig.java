package com.films.ngoma.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	private static final String[] DOCUMENTATION = {
			"/v3/api-docs", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/error"
	};

	/** Renvoie un 401 JSON au format ApplicationErreur quand aucun utilisateur authentifié n'est trouvé. */
	private static final AuthenticationEntryPoint SESSION_EXPIREE = (request, response, ex) -> {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.getWriter().write("{\"code\":9002,\"type\":\"ERROR\",\"message\":\"Veuillez vous connecter.\",\"parametres\":[]}");
	};

	private final JwtRequestFilter jwtRequestFilter;
	private final LogoutHandler logoutHandler;
	private final String api;

	public SecurityConfig(JwtRequestFilter jwtRequestFilter, LogoutHandler logoutHandler,
						  @Value("${api.prefix}") String apiPrefix) {
		this.jwtRequestFilter = jwtRequestFilter;
		this.logoutHandler = logoutHandler;
		this.api = apiPrefix;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(AbstractHttpConfigurer::disable)
				.cors(Customizer.withDefaults())
				.exceptionHandling(e -> e.authenticationEntryPoint(SESSION_EXPIREE))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(DOCUMENTATION).permitAll()
						.requestMatchers(api + "/securite/**").permitAll()
						// Back-office : administrateurs uniquement (en plus de @PreAuthorize sur les contrôleurs)
						.requestMatchers(api + "/admin/**").hasRole("ADMIN")
						// Images publiques (affiches, bandeaux) servies par WebConfig
						.requestMatchers(HttpMethod.GET, "/medias/**").permitAll()
						// Catalogue consultable sans compte
						.requestMatchers(HttpMethod.GET, api + "/film/**", api + "/pays/**",
								api + "/genre/**", api + "/pass/**").permitAll()
						// Notification (webhook) de l'opérateur de paiement, authentifiée par signature
						.requestMatchers(HttpMethod.POST, api + "/paiement/notification").permitAll()
						.anyRequest().authenticated())
				.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
				.logout(logout -> logout
						.logoutUrl(api + "/auth/logout")
						.addLogoutHandler(logoutHandler)
						.logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext()));
		return http.build();
	}
}
