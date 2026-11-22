package com.films.ngoma.utils;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ImageUtilsTest {

	public static final byte[] PNG = {(byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1A, '\n', 0, 0, 0, 13, 'I', 'H', 'D', 'R'};
	public static final byte[] JPEG = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0, 16};

	@Test
	void reconnaitLesImagesParLeurSignature() {
		assertEquals("image/png", ImageUtils.detecterType(PNG));
		assertEquals("image/jpeg", ImageUtils.detecterType(JPEG));
		assertEquals("image/webp", ImageUtils.detecterType("RIFF\0\0\0\0WEBPVP8 ".getBytes(StandardCharsets.US_ASCII)));
	}

	@Test
	void refuseCeQuiNEstPasUneImage() {
		assertNull(ImageUtils.detecterType("<svg xmlns='http://www.w3.org/2000/svg'/>".getBytes(StandardCharsets.UTF_8)));
		assertNull(ImageUtils.detecterType("GIF89a......".getBytes(StandardCharsets.US_ASCII)));
		assertNull(ImageUtils.detecterType(new byte[0]));
		assertNull(ImageUtils.detecterType(null));
	}
}
