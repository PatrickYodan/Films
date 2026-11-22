import { TestBed } from '@angular/core/testing';
import { DUREE_MIN_MS, PreloaderService } from './preloader.service';

describe('PreloaderService', () => {
	let preloader: HTMLDivElement;

	beforeEach(() => {
		vi.useFakeTimers();
		// Page ouverte à l'instant : toute la durée minimale reste à afficher
		vi.spyOn(performance, 'now').mockReturnValue(0);
		document.documentElement.classList.add('ngoma-chargement');
		preloader = document.createElement('div');
		preloader.id = 'ngoma-preloader';
		document.body.appendChild(preloader);
	});

	afterEach(() => {
		vi.useRealTimers();
		vi.restoreAllMocks();
		preloader.remove();
		document.documentElement.classList.remove('ngoma-chargement');
	});

	it('reste affiché pendant la durée minimale, puis sort et se retire', async () => {
		TestBed.inject(PreloaderService).masquer();

		await vi.advanceTimersByTimeAsync(DUREE_MIN_MS - 200);
		expect(preloader.classList.contains('ngp-termine')).toBe(false);

		await vi.advanceTimersByTimeAsync(300);
		expect(preloader.classList.contains('ngp-termine')).toBe(true);

		await vi.advanceTimersByTimeAsync(300);
		expect(preloader.classList.contains('ngp-sortie')).toBe(true);
		expect(document.documentElement.classList.contains('ngoma-chargement')).toBe(false);

		await vi.advanceTimersByTimeAsync(1300);
		expect(document.getElementById('ngoma-preloader')).toBeNull();
	});

	it("se passe tout de suite d'un clic", async () => {
		TestBed.inject(PreloaderService).masquer();
		await vi.advanceTimersByTimeAsync(1300); // polices prêtes
		expect(preloader.classList.contains('ngp-termine')).toBe(false);

		preloader.dispatchEvent(new Event('pointerdown'));
		await vi.advanceTimersByTimeAsync(10);
		expect(preloader.classList.contains('ngp-termine')).toBe(true);
	});

	it("ne fait rien de plus s'il est appelé plusieurs fois", async () => {
		const service = TestBed.inject(PreloaderService);
		service.masquer();
		service.masquer();
		await vi.advanceTimersByTimeAsync(DUREE_MIN_MS + 2000);
		expect(document.getElementById('ngoma-preloader')).toBeNull();
	});
});
