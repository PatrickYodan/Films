import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { environment } from '../../environments/environment';
import { AuthService } from '../services/auth.service';
import { ApplicationMessageService } from '../services/message.service';
import { NavigationService } from '../services/navigation.service';

const URL_ABSOLUE = /^https?:\/\//i;

/**
 * Préfixe les URL par l'adresse de l'API, ajoute le jeton JWT et gère la session expirée.
 */
export const apiInterceptor: HttpInterceptorFn = (req, next) => {
	if (req.url.includes('assets/') || URL_ABSOLUE.test(req.url)) {
		return next(req);
	}

	const authService = inject(AuthService);
	const navigationService = inject(NavigationService);
	const messageService = inject(ApplicationMessageService);
	const token = authService.getToken();

	const requete = req.clone({
		url: (environment.apiUrl + '/' + req.url).replace(/([^:]\/)\/+/g, '$1'),
		setHeaders: token ? { Authorization: 'Bearer ' + token } : {},
	});

	return next(requete).pipe(
		catchError((err: HttpErrorResponse) => {
			if (err.status === 401 && !req.url.includes('/securite/')) {
				authService.deconnecter();
				navigationService.goTologin();
			} else if (err.status === 0 || err.status >= 500) {
				messageService.erreur(
					err.status === 0
						? 'Connexion au serveur impossible. Vérifiez votre connexion internet.'
						: 'Une erreur inattendue est survenue. Veuillez réessayer plus tard.'
				);
			}
			return throwError(() => err);
		})
	);
};
