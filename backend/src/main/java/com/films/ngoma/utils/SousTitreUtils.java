package com.films.ngoma.utils;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * Préparation des fichiers de sous-titres : le navigateur ne lit que le format WebVTT (.vtt),
 * mais les équipes reçoivent souvent des fichiers SubRip (.srt), parfois encodés en Windows-1252.
 */
public final class SousTitreUtils {

	private static final Pattern HORODATAGE_SRT = Pattern.compile("(\\d{2}:\\d{2}:\\d{2}),(\\d{3})");
	private static final Charset WINDOWS_1252 = Charset.forName("windows-1252");

	private SousTitreUtils() {
	}

	/**
	 * Convertit un fichier de sous-titres (WebVTT ou SubRip) en texte WebVTT.
	 *
	 * @throws IllegalArgumentException si le contenu n'est pas un fichier de sous-titres reconnu
	 */
	public static String versWebVtt(byte[] contenu) {
		String texte = decoder(contenu)
				.replace("﻿", "")
				.replace("\r\n", "\n")
				.replace('\r', '\n')
				.strip();
		if (texte.startsWith("WEBVTT")) {
			return texte + "\n";
		}
		if (!texte.contains("-->")) {
			throw new IllegalArgumentException("Fichier de sous-titres non reconnu");
		}
		// SubRip : la seule différence de syntaxe est la virgule des millisecondes (00:00:01,500 → 00:00:01.500)
		return "WEBVTT\n\n" + HORODATAGE_SRT.matcher(texte).replaceAll("$1.$2") + "\n";
	}

	/** UTF-8 si le fichier est valide en UTF-8, sinon Windows-1252 (fréquent pour les .srt en français). */
	private static String decoder(byte[] contenu) {
		try {
			return StandardCharsets.UTF_8.newDecoder()
					.onMalformedInput(CodingErrorAction.REPORT)
					.onUnmappableCharacter(CodingErrorAction.REPORT)
					.decode(ByteBuffer.wrap(contenu))
					.toString();
		} catch (CharacterCodingException e) {
			return new String(contenu, WINDOWS_1252);
		}
	}
}
