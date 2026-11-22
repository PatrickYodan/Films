import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { FilmResume } from '../../../models/film.model';
import { HeroCarrouselComponent } from './hero-carrousel.component';

function film(id: number, autres: Partial<FilmResume> = {}): FilmResume {
	return {
		id,
		slug: 'film-' + id,
		titre: 'Film ' + id,
		synopsisCourt: null,
		annee: 2025,
		dureeMinutes: 95,
		classificationAge: null,
		afficheUrl: null,
		bandeauUrl: null,
		gratuit: false,
		exclusivite: false,
		genres: ['Drame'],
		pays: ['CI'],
		...autres,
	};
}

describe('HeroCarrouselComponent', () => {
	async function creer(films: FilmResume[]) {
		await TestBed.configureTestingModule({ imports: [HeroCarrouselComponent], providers: [provideRouter([])] }).compileComponents();
		const fixture = TestBed.createComponent(HeroCarrouselComponent);
		fixture.componentRef.setInput('films', films);
		await fixture.whenStable();
		return fixture;
	}

	it('affiche le premier film puis boucle dans les deux sens', async () => {
		const fixture = await creer([film(1), film(2), film(3)]);
		const c = fixture.componentInstance;
		const titre = () => (fixture.nativeElement as HTMLElement).querySelector('h1')?.textContent;

		expect(titre()).toContain('Film 1');
		c.suivant();
		c.suivant();
		await fixture.whenStable();
		expect(titre()).toContain('Film 3');
		c.suivant();
		await fixture.whenStable();
		expect(titre()).toContain('Film 1');
		c.precedent();
		await fixture.whenStable();
		expect(titre()).toContain('Film 3');
	});

	it('propose une vignette par film et va au film choisi', async () => {
		const fixture = await creer([film(1), film(2), film(3)]);
		const element = fixture.nativeElement as HTMLElement;
		const vignettes = element.querySelectorAll<HTMLButtonElement>('[role=tab]');
		expect(vignettes.length).toBe(3);

		vignettes[1].click();
		await fixture.whenStable();
		expect(element.querySelector('h1')?.textContent).toContain('Film 2');
		expect(vignettes[1].getAttribute('aria-selected')).toBe('true');
	});

	it('libellé selon le film', async () => {
		const fixture = await creer([film(1)]);
		const c = fixture.componentInstance;
		expect(c.libelle(film(1, { exclusivite: true }))).toBe('À la une · Exclusivité');
		expect(c.libelle(film(2, { gratuit: true }))).toBe('À la une · Gratuit');
		expect(c.libelle(film(3))).toBe('À la une');
	});

	it("n'affiche pas de commandes pour un seul film", async () => {
		const fixture = await creer([film(1)]);
		expect((fixture.nativeElement as HTMLElement).querySelector('.commandes')).toBeNull();
	});
});
