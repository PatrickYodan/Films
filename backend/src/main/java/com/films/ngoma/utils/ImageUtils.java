package com.films.ngoma.utils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Contrôle des images envoyées : le format est déduit de la signature du fichier,
 * jamais de l'extension ni du type annoncé par le navigateur.
 */
public final class ImageUtils {

	private static final byte[] SIGNATURE_PNG = {(byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1A, '\n'};

	private ImageUtils() {
	}

	/**
	 * @return le type MIME (image/jpeg, image/png ou image/webp), ou null si le format n'est pas accepté.
	 */
	public static String detecterType(byte[] c) {
		if (c == null) {
			return null;
		}
		if (c.length > 3 && (c[0] & 0xFF) == 0xFF && (c[1] & 0xFF) == 0xD8 && (c[2] & 0xFF) == 0xFF) {
			return "image/jpeg";
		}
		if (c.length > 8 && Arrays.equals(Arrays.copyOf(c, 8), SIGNATURE_PNG)) {
			return "image/png";
		}
		if (c.length > 12 && "RIFF".equals(new String(c, 0, 4, StandardCharsets.US_ASCII))
				&& "WEBP".equals(new String(c, 8, 4, StandardCharsets.US_ASCII))) {
			return "image/webp";
		}
		return null;
	}

	/** @return l'extension de fichier correspondant au type MIME accepté. */
	public static String extension(String typeMime) {
		return switch (typeMime) {
			case "image/jpeg" -> "jpg";
			case "image/png" -> "png";
			case "image/webp" -> "webp";
			default -> throw new IllegalArgumentException("Type d'image non pris en charge : " + typeMime);
		};
	}
}
