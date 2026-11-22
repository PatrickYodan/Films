package com.films.ngoma.utils;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SlugUtilsTest {

	@Test
	void slugifierRetireAccentsApostrophesEtMajuscules() {
		assertEquals("l-ombre-du-baobab", SlugUtils.slugifier("L'Ombre du baobab"));
		assertEquals("cote-d-ivoire-ete-2025", SlugUtils.slugifier("  Côte d’Ivoire : été 2025 !  "));
		assertEquals("coeur-de-l-afrique", SlugUtils.slugifier("Cœur de l'Afrique"));
	}

	@Test
	void slugifierDonneUneValeurParDefautSiRienNeReste() {
		assertEquals("film", SlugUtils.slugifier("!!!"));
		assertEquals("film", SlugUtils.slugifier(null));
	}

	@Test
	void slugifierTronqueLesTitresTresLongs() {
		String slug = SlugUtils.slugifier("mot ".repeat(100));
		assertTrue(slug.length() <= SlugUtils.LONGUEUR_MAX);
		assertTrue(!slug.endsWith("-"));
	}

	@Test
	void rendreUniqueAjouteUnSuffixe() {
		assertEquals("la-dot", SlugUtils.rendreUnique("la-dot", List.of()));
		assertEquals("la-dot-2", SlugUtils.rendreUnique("la-dot", List.of("la-dot")));
		assertEquals("la-dot-4", SlugUtils.rendreUnique("la-dot", List.of("la-dot", "la-dot-2", "la-dot-3")));
		assertEquals("la-dot-2", SlugUtils.rendreUnique("la-dot", List.of("la-dot", "la-dot-3")));
	}
}
