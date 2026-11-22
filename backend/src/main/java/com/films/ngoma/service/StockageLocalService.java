package com.films.ngoma.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.regex.Pattern;

/**
 * Stockage sur le disque local (développement). Les fichiers sont servis en statique sur /medias/** (voir WebConfig).
 */
@Service
public class StockageLocalService implements StockageService {

	private static final Logger log = LoggerFactory.getLogger(StockageLocalService.class);
	/** Segments autorisés : pas de « .. », pas de chemin absolu, pas de caractères spéciaux. */
	private static final Pattern DOSSIER_VALIDE = Pattern.compile("^[a-z0-9-]+(/[a-z0-9-]+)*$");
	private static final Pattern NOM_VALIDE = Pattern.compile("^[a-z0-9-]+\\.[a-z0-9]+$");

	private final Path racine;
	private final String urlPublique;

	public StockageLocalService(@Value("${ngoma.stockage.dossier}") String dossier,
								@Value("${ngoma.stockage.url_publique}") String urlPublique) {
		this.racine = Path.of(dossier).toAbsolutePath().normalize();
		this.urlPublique = urlPublique.replaceAll("/+$", "");
		try {
			Files.createDirectories(racine);
		} catch (IOException e) {
			throw new UncheckedIOException("Impossible de créer le dossier de stockage " + racine, e);
		}
	}

	@Override
	public String enregistrer(String dossier, String nom, byte[] octets, String typeMime) {
		if (!DOSSIER_VALIDE.matcher(dossier).matches() || !NOM_VALIDE.matcher(nom).matches()) {
			throw new IllegalArgumentException("Chemin de stockage invalide : " + dossier + "/" + nom);
		}
		Path cible = resoudre(dossier + "/" + nom);
		try {
			Files.createDirectories(cible.getParent());
			Path temporaire = Files.createTempFile(cible.getParent(), "envoi-", ".tmp");
			Files.write(temporaire, octets);
			Files.move(temporaire, cible, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
		} catch (IOException e) {
			throw new UncheckedIOException("Échec de l'enregistrement de " + cible, e);
		}
		return urlPublique + "/" + dossier + "/" + nom;
	}

	@Override
	public FichierStocke enregistrerFlux(String dossier, String nom, InputStream flux, long tailleMax, String typeMime) {
		if (!DOSSIER_VALIDE.matcher(dossier).matches() || !NOM_VALIDE.matcher(nom).matches()) {
			throw new IllegalArgumentException("Chemin de stockage invalide : " + dossier + "/" + nom);
		}
		Path cible = resoudre(dossier + "/" + nom);
		Path temporaire = null;
		try {
			Files.createDirectories(cible.getParent());
			temporaire = Files.createTempFile(cible.getParent(), "envoi-", ".tmp");
			long total = 0;
			byte[] tampon = new byte[1 << 16];
			try (OutputStream sortie = Files.newOutputStream(temporaire)) {
				int lus;
				while ((lus = flux.read(tampon)) != -1) {
					total += lus;
					if (total > tailleMax) {
						throw new TailleMaximaleDepassee();
					}
					sortie.write(tampon, 0, lus);
				}
			}
			Files.move(temporaire, cible, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
			return new FichierStocke(urlPublique + "/" + dossier + "/" + nom, total);
		} catch (IOException e) {
			throw new UncheckedIOException("Échec de l'enregistrement de " + cible, e);
		} finally {
			// Envoi interrompu, trop volumineux ou en échec : on ne laisse pas de fichier partiel.
			if (temporaire != null) {
				try {
					Files.deleteIfExists(temporaire);
				} catch (IOException e) {
					log.warn("Fichier temporaire non supprimé : {}", temporaire);
				}
			}
		}
	}

	@Override
	public void supprimer(String url) {
		if (url == null || !url.startsWith(urlPublique + "/")) {
			return;
		}
		try {
			Files.deleteIfExists(resoudre(url.substring(urlPublique.length() + 1)));
		} catch (IOException | IllegalArgumentException e) {
			// Un fichier orphelin n'est pas bloquant : on trace et on continue.
			log.warn("Suppression impossible de {} : {}", url, e.getMessage());
		}
	}

	/** Résout un chemin relatif en refusant toute sortie du dossier racine. */
	private Path resoudre(String relatif) {
		Path chemin = racine.resolve(relatif).normalize();
		if (!chemin.startsWith(racine)) {
			throw new IllegalArgumentException("Chemin hors du dossier de stockage : " + relatif);
		}
		return chemin;
	}
}
