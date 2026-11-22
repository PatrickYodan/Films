package com.films.ngoma.utils;

import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SousTitreUtilsTest {

	@Test
	void convertitUnFichierSrtEnWebVtt() {
		String srt = "1\r\n00:00:01,500 --> 00:00:04,000\r\nBonjour Awa.\r\n\r\n2\r\n00:01:02,000 --> 00:01:05,250\r\nOù vas-tu ?\r\n";
		String vtt = SousTitreUtils.versWebVtt(srt.getBytes(StandardCharsets.UTF_8));
		assertTrue(vtt.startsWith("WEBVTT\n\n"));
		assertTrue(vtt.contains("00:00:01.500 --> 00:00:04.000"));
		assertTrue(vtt.contains("00:01:02.000 --> 00:01:05.250"));
		assertTrue(vtt.contains("Où vas-tu ?"));
		assertTrue(!vtt.contains("\r"));
	}

	@Test
	void garderUnFichierWebVttTelQuel() {
		String vtt = "﻿WEBVTT\n\n00:00.000 --> 00:02.000\nSalut\n";
		assertEquals("WEBVTT\n\n00:00.000 --> 00:02.000\nSalut\n", SousTitreUtils.versWebVtt(vtt.getBytes(StandardCharsets.UTF_8)));
	}

	@Test
	void litLesFichiersEncodesEnWindows1252() {
		byte[] srt = "1\n00:00:01,000 --> 00:00:02,000\nL'été à Abidjan\n".getBytes(Charset.forName("windows-1252"));
		assertTrue(SousTitreUtils.versWebVtt(srt).contains("L'été à Abidjan"));
	}

	@Test
	void refuseUnFichierQuiNEstPasDesSousTitres() {
		assertThrows(IllegalArgumentException.class,
				() -> SousTitreUtils.versWebVtt("<html>pas des sous-titres</html>".getBytes(StandardCharsets.UTF_8)));
	}
}
