import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { finalize } from 'rxjs';
import { ChargementService } from '../services/chargement.service';

/** Alimente la barre de chargement globale (hors fichiers statiques et suivi de paiement). */
export const chargementInterceptor: HttpInterceptorFn = (req, next) => {
	if (req.url.includes('assets/') || (req.method === 'GET' && req.url.includes('/ws/paiement/'))) {
		return next(req);
	}
	const chargementService = inject(ChargementService);
	chargementService.demarrer();
	return next(req).pipe(finalize(() => chargementService.terminer()));
};
