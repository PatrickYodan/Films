package com.films.ngoma.domain;

import com.films.ngoma.enums.TypeContenu;

import java.time.LocalDate;

/**
 * Informations éditoriales d'un film, saisies dans le back-office.
 * Objet valeur utilisé par {@link Film#creer} et {@link Film#modifier} (la validation est faite par le DTO d'entrée).
 */
public record InformationsFilm(String titre, TypeContenu type, String synopsisCourt, String synopsis,
							   Integer annee, Integer dureeMinutes, String classificationAge, LocalDate dateSortie,
							   boolean gratuit, boolean exclusivite, boolean telechargeable, String bandeAnnonceUrl) {
}
