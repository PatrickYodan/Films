import { Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';

@Injectable({
	providedIn: 'root',
})
export class NavigationService {
	readonly URL_CONNEXION = '/connexion';
	readonly URL_ABONNEMENT = '/abonnement';

	private readonly router = inject(Router);

	goTo(route: string): Promise<boolean> {
		return this.router.navigateByUrl(route);
	}

	goToAccueil(): Promise<boolean> {
		return this.goTo('/');
	}

	/**
	 * Redirige vers la connexion en mémorisant la page demandée.
	 */
	goTologin(retour: string = this.router.url): Promise<boolean> {
		const queryParams = retour && !retour.startsWith(this.URL_CONNEXION) ? { retour } : {};
		return this.router.navigate([this.URL_CONNEXION], { queryParams });
	}

	isLogin(): boolean {
		return this.router.url.startsWith(this.URL_CONNEXION);
	}
}
