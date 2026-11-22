import { HttpEventType, provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { AdminFilmService } from './admin-film.service';

describe('AdminFilmService', () => {
	let service: AdminFilmService;
	let http: HttpTestingController;

	beforeEach(() => {
		TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
		service = TestBed.inject(AdminFilmService);
		http = TestBed.inject(HttpTestingController);
	});

	afterEach(() => http.verify());

	it('liste avec recherche, statut et pagination', () => {
		service.lister('  baobab ', 'BROUILLON', 2, 20).subscribe();
		const req = http.expectOne((r) => r.url === '/ws/admin/film/lister');
		expect(req.request.method).toBe('GET');
		expect(req.request.params.get('motCle')).toBe('baobab');
		expect(req.request.params.get('statut')).toBe('BROUILLON');
		expect(req.request.params.get('page')).toBe('2');
		expect(req.request.params.get('taille')).toBe('20');
		req.flush({ contenu: [], page: 2, taille: 20, total: 0, totalPages: 0 });
	});

	it("n'envoie pas les filtres vides", () => {
		service.lister('', null).subscribe();
		const req = http.expectOne((r) => r.url === '/ws/admin/film/lister');
		expect(req.request.params.has('motCle')).toBe(false);
		expect(req.request.params.has('statut')).toBe(false);
		req.flush({ contenu: [], page: 0, taille: 20, total: 0, totalPages: 0 });
	});

	it('appelle les bonnes URL pour chaque action', () => {
		service.detail(7).subscribe();
		service.modifier(7, {} as never).subscribe();
		service.publier(7).subscribe();
		service.depublier(7).subscribe();
		service.supprimer(7).subscribe();
		service.remplacerDistribution(7, []).subscribe();
		service.remplacerVideos(7, []).subscribe();
		service.remplacerSousTitres(7, []).subscribe();
		const attendus: [string, string][] = [
			['GET', '/ws/admin/film/7'],
			['PUT', '/ws/admin/film/7'],
			['POST', '/ws/admin/film/7/publier'],
			['POST', '/ws/admin/film/7/depublier'],
			['DELETE', '/ws/admin/film/7'],
			['PUT', '/ws/admin/film/7/distribution'],
			['PUT', '/ws/admin/film/7/videos'],
			['PUT', '/ws/admin/film/7/sous-titres'],
		];
		for (const [methode, url] of attendus) {
			http.expectOne((r) => r.method === methode && r.url === url).flush(null);
		}
	});

	it('envoie un fichier vidéo en flux brut, par qualité', () => {
		const fichier = new File([new Uint8Array([0, 0, 0, 32])], 'film.mp4', { type: 'video/mp4' });
		service.envoyerVideo(4, 'HD_1080P', fichier).subscribe();
		const req = http.expectOne('/ws/admin/film/4/videos/HD_1080P/fichier');
		expect(req.request.method).toBe('PUT');
		expect(req.request.headers.get('Content-Type')).toBe('application/octet-stream');
		expect(req.request.reportProgress).toBe(true);
		expect(req.request.body).toBe(fichier);
		req.flush({ id: 4 });
	});

	it('envoie un fichier de sous-titres avec sa langue et son libellé', () => {
		const fichier = new File(['1\n00:00:01,000 --> 00:00:02,000\nBonjour\n'], 'fr.srt');
		service.envoyerSousTitre(5, 'fr', 'Français', fichier).subscribe();
		const req = http.expectOne('/ws/admin/film/5/sous-titres/fichier');
		expect(req.request.method).toBe('POST');
		const corps = req.request.body as FormData;
		expect(corps.get('langue')).toBe('fr');
		expect(corps.get('libelle')).toBe('Français');
		expect(corps.get('fichier')).toBeInstanceOf(File);
		req.flush({ id: 5 });
	});

	it("envoie l'image en multipart avec suivi de progression", () => {
		const evenements: HttpEventType[] = [];
		const fichier = new File([new Uint8Array([0x89, 0x50])], 'affiche.png', { type: 'image/png' });
		service.envoyerImage(3, 'bandeau', fichier).subscribe((e) => evenements.push(e.type));
		const req = http.expectOne('/ws/admin/film/3/bandeau');
		expect(req.request.method).toBe('POST');
		expect(req.request.reportProgress).toBe(true);
		expect((req.request.body as FormData).get('fichier')).toBeInstanceOf(File);
		req.flush({ id: 3 });
		expect(evenements).toContain(HttpEventType.Response);
	});
});
