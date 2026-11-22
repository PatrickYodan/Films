package com.films.ngoma.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration Spring MVC : CORS pour le frontend Angular et fichiers publics du stockage local (/medias/**).
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final String clientLocal;
	private final String clientOnline;
	private final String dossierStockage;

	public WebConfig(@Value("${client.base_url.local}") String clientLocal,
					 @Value("${client.base_url.online}") String clientOnline,
					 @Value("${ngoma.stockage.dossier}") String dossierStockage) {
		this.clientLocal = clientLocal;
		this.clientOnline = clientOnline;
		this.dossierStockage = dossierStockage;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String emplacement = Path.of(dossierStockage).toAbsolutePath().normalize().toUri().toString();
		registry.addResourceHandler("/medias/**")
				.addResourceLocations(emplacement.endsWith("/") ? emplacement : emplacement + "/");
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins(clientLocal, clientOnline)
				.allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
				.allowedHeaders("*")
				.allowCredentials(true);
	}
}
