import { computed, signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { UtilisateurConnecte } from '../../models/utilisateur.model';
import { AuthService } from '../../services/auth.service';
import { FooterComponent } from './footer.component';

describe('FooterComponent', () => {
	const utilisateur = signal<UtilisateurConnecte | null>(null);

	async function creer() {
		await TestBed.configureTestingModule({
			imports: [FooterComponent],
			providers: [
				provideRouter([]),
				{ provide: AuthService, useValue: { utilisateur, connecte: computed(() => utilisateur() !== null) } },
			],
		}).compileComponents();
		const fixture = TestBed.createComponent(FooterComponent);
		await fixture.whenStable();
		return fixture.nativeElement as HTMLElement;
	}

	const liens = (element: HTMLElement) => [...element.querySelectorAll('nav a')].map((a) => a.textContent?.trim());

	beforeEach(() => utilisateur.set(null));

	it('propose la connexion aux visiteurs', async () => {
		const element = await creer();
		expect(liens(element)).toContain('Se connecter');
		expect(liens(element)).toContain('Créer un compte');
	});

	it("masque la connexion une fois connecté et affiche l'espace de l'utilisateur", async () => {
		utilisateur.set({ id: 1, nom: 'Awa', email: 'awa@exemple.ci', role: 'ABONNE' });
		const element = await creer();
		expect(liens(element)).not.toContain('Se connecter');
		expect(liens(element)).not.toContain('Créer un compte');
		expect(liens(element)).toContain('Mon compte');
		expect(liens(element)).toContain('Mon pass');
		expect(liens(element)).not.toContain('Administration');
	});

	it('affiche les réseaux sociaux avec des liens accessibles ouvrant un nouvel onglet', async () => {
		const element = await creer();
		const reseaux = [...element.querySelectorAll<HTMLAnchorElement>('.reseaux a')];
		expect(reseaux.map((a) => a.getAttribute('aria-label'))).toEqual([
			'NGOMA sur WhatsApp (nouvel onglet)',
			'NGOMA sur YouTube (nouvel onglet)',
			'NGOMA sur Facebook (nouvel onglet)',
		]);
		expect(reseaux.every((a) => a.target === '_blank' && a.rel.includes('noopener'))).toBe(true);
		expect(reseaux.every((a) => a.querySelector('app-icone svg path'))).toBe(true);
	});
});
