import { inject } from '@angular/core';
import { CanActivateChildFn, CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../auth.service';

/**
 * Réserve la route aux administrateurs :
 * non connecté → page de connexion (avec retour) ; connecté sans le rôle ADMIN → page « Accès interdit ».
 * Le backend vérifie aussi le rôle sur /ws/admin/** : ce garde ne sert qu'à l'ergonomie.
 */
export const adminGuard: CanActivateFn = (_route, state) => {
	const authService = inject(AuthService);
	const router = inject(Router);
	if (!authService.isAuthenticated()) {
		return router.createUrlTree(['/connexion'], { queryParams: { retour: state.url } });
	}
	return authService.utilisateur()?.role === 'ADMIN' ? true : router.createUrlTree(['/acces-interdit']);
};

export const adminChildGuard: CanActivateChildFn = (route, state) => adminGuard(route, state);
