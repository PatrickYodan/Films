import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree, provideRouter } from '@angular/router';
import { UtilisateurConnecte } from '../../models/utilisateur.model';
import { AuthService } from '../auth.service';
import { adminGuard } from './admin-guard.service';

describe('adminGuard', () => {
	const utilisateur = signal<UtilisateurConnecte | null>(null);
	let connecte = false;

	beforeEach(() => {
		utilisateur.set(null);
		connecte = false;
		TestBed.configureTestingModule({
			providers: [
				provideRouter([]),
				{ provide: AuthService, useValue: { isAuthenticated: () => connecte, utilisateur } },
			],
		});
	});

	function executer(): boolean | UrlTree {
		return TestBed.runInInjectionContext(() =>
			adminGuard({} as ActivatedRouteSnapshot, { url: '/admin/films' } as RouterStateSnapshot)
		) as boolean | UrlTree;
	}

	function versUrl(resultat: boolean | UrlTree): string {
		return TestBed.inject(Router).serializeUrl(resultat as UrlTree);
	}

	it('redirige un visiteur non connecté vers la connexion avec retour', () => {
		expect(versUrl(executer())).toBe('/connexion?retour=%2Fadmin%2Ffilms');
	});

	it('refuse un abonné et le redirige vers la page « Accès interdit »', () => {
		connecte = true;
		utilisateur.set({ id: 2, nom: 'Abonné', email: 'a@exemple.ci', role: 'ABONNE' });
		expect(versUrl(executer())).toBe('/acces-interdit');
	});

	it('laisse passer un administrateur', () => {
		connecte = true;
		utilisateur.set({ id: 1, nom: 'Admin', email: 'admin@exemple.ci', role: 'ADMIN' });
		expect(executer()).toBe(true);
	});
});
