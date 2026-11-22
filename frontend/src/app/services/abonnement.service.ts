import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Abonnement } from '../models/abonnement.model';

@Injectable({
	providedIn: 'root',
})
export class AbonnementService {
	private readonly url = '/ws/abonnement';
	private readonly http = inject(HttpClient);

	/** @return l'abonnement actif, ou null (réponse 204) s'il n'y en a pas. */
	courant(): Observable<Abonnement | null> {
		return this.http.get<Abonnement | null>(this.url + '/courant');
	}
}
