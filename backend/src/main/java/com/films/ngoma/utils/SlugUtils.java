package com.films.ngoma.utils;

import java.text.Normalizer;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.HashSet;

/**
 * Génération de slugs d'URL : « L'Ombre du baobab » → « l-ombre-du-baobab ».
 */
public final class SlugUtils {

	/** Longueur maximale d'un slug, suffixe compris (colonne VARCHAR(255)). */
	public static final int LONGUEUR_MAX = 200;
	private static final String SLUG_PAR_DEFAUT = "film";

	private SlugUtils() {
	}

	/**
	 * @return le slug du texte : minuscules, sans accents, mots séparés par des tirets.
	 */
	public static String slugifier(String texte) {
		if (texte == null) {
			return SLUG_PAR_DEFAUT;
		}
		String slug = Normalizer.normalize(texte.trim(), Normalizer.Form.NFD)
				.replaceAll("\\p{M}+", "")
				.replace("œ", "oe").replace("Œ", "oe").replace("æ", "ae").replace("Æ", "ae").replace("ß", "ss")
				.toLowerCase(Locale.ROOT)
				.replaceAll("[^a-z0-9]+", "-")
				.replaceAll("(^-+)|(-+$)", "");
		if (slug.length() > LONGUEUR_MAX) {
			slug = slug.substring(0, LONGUEUR_MAX).replaceAll("-+$", "");
		}
		return slug.isEmpty() ? SLUG_PAR_DEFAUT : slug;
	}

	/**
	 * @param base       slug souhaité
	 * @param slugsPris  slugs déjà utilisés commençant par {@code base}
	 * @return {@code base} s'il est libre, sinon {@code base-2}, {@code base-3}…
	 */
	public static String rendreUnique(String base, Collection<String> slugsPris) {
		Set<String> pris = new HashSet<>(slugsPris);
		if (!pris.contains(base)) {
			return base;
		}
		int suffixe = 2;
		while (pris.contains(base + "-" + suffixe)) {
			suffixe++;
		}
		return base + "-" + suffixe;
	}
}
