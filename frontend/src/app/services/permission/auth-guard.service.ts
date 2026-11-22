import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../auth.service';

/**
 * Réserve la route aux utilisateurs connectés ; sinon redirige vers la connexion
 * en mémorisant la page demandée.
 */
export const authGuard: CanActivateFn = (_route, state) => {
	if (inject(AuthService).isAuthenticated()) {
		return true;
	}
	return inject(Router).createUrlTree(['/connexion'], { queryParams: { retour: state.url } });
};
