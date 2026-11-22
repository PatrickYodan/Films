import { TestBed } from '@angular/core/testing';
import { NonNullableFormBuilder } from '@angular/forms';
import { creerFormulaireFilm } from './admin-film-form.component';

describe('Formulaire de film (validation)', () => {
	function formulaire() {
		return creerFormulaireFilm(TestBed.inject(NonNullableFormBuilder));
	}

	it('est invalide vide : titre, genre et pays obligatoires', () => {
		const f = formulaire();
		expect(f.valid).toBe(false);
		expect(f.controls.titre.hasError('required')).toBe(true);
		expect(f.controls.genreIds.hasError('auMoinsUn')).toBe(true);
		expect(f.controls.paysCodes.hasError('auMoinsUn')).toBe(true);
	});

	it('est valide avec titre, un genre et un pays', () => {
		const f = formulaire();
		f.patchValue({ titre: 'La Dot', genreIds: [1], paysCodes: ['CI'] });
		expect(f.valid).toBe(true);
	});

	it("contrôle l'année, la durée et l'URL de bande-annonce", () => {
		const f = formulaire();
		f.patchValue({ annee: 1800, dureeMinutes: 0, bandeAnnonceUrl: 'pas une url' });
		expect(f.controls.annee.hasError('min')).toBe(true);
		expect(f.controls.dureeMinutes.hasError('min')).toBe(true);
		expect(f.controls.bandeAnnonceUrl.hasError('pattern')).toBe(true);
		f.patchValue({ annee: 2025, dureeMinutes: 97, bandeAnnonceUrl: 'https://youtu.be/abc' });
		expect(f.controls.annee.valid && f.controls.dureeMinutes.valid && f.controls.bandeAnnonceUrl.valid).toBe(true);
	});

	it('accepte un lien HLS ou MP4, relatif ou en http(s)', () => {
		const video = formulaire().controls.videos.controls.STANDARD_480P;
		for (const valide of [
			'films/la-dot/480p/index.m3u8',
			'https://cdn.exemple.ci/la-dot/index.m3u8',
			'https://cdn.exemple.ci/la-dot/film.mp4',
			'http://localhost:8090/medias/films/la-dot/videos/standard-480p-1a2b.mp4',
		]) {
			video.setValue(valide);
			expect(video.valid).toBe(true);
		}
	});

	it('refuse un chemin HLS invalide ou remontant dans les dossiers', () => {
		const videos = formulaire().controls.videos.controls;
		videos.STANDARD_480P.setValue('films/la-dot/480p/index.m3u8');
		expect(videos.STANDARD_480P.valid).toBe(true);
		videos.STANDARD_480P.setValue('https://cdn.exemple.ci/la-dot/index.m3u8');
		expect(videos.STANDARD_480P.valid).toBe(true);
		for (const invalide of ['../secret/index.m3u8', 'films/la-dot/video.avi', 'ftp://cdn.exemple.ci/x.m3u8', 'films/../x.mp4']) {
			videos.STANDARD_480P.setValue(invalide);
			expect(videos.STANDARD_480P.hasError('pattern')).toBe(true);
		}
	});
});
