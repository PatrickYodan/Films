import { HttpErrorResponse } from '@angular/common/http';

/** Erreur renvoyée par l'API (voir ApplicationErreur côté backend). */
export interface ApplicationErreur {
	code: number;
	type: string;
	message: string;
	parametres: string[];
}

/** Extrait un message lisible d'une erreur HTTP. */
export function messageErreur(err: unknown, parDefaut = 'Une erreur est survenue. Veuillez réessayer.'): string {
	if (err instanceof HttpErrorResponse) {
		if (err.status === 0) {
			return 'Connexion au serveur impossible. Vérifiez votre connexion internet.';
		}
		const corps = err.error as Partial<ApplicationErreur> & { detail?: string } | null;
		return corps?.message ?? corps?.detail ?? parDefaut;
	}
	return parDefaut;
}
