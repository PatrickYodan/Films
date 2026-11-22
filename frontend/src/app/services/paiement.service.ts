import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { InitierPaiement, Paiement } from '../models/paiement.model';

@Injectable({
	providedIn: 'root',
})
export class PaiementService {
	private readonly url = '/ws/paiement';
	private readonly http = inject(HttpClient);

	initier(demande: InitierPaiement): Observable<Paiement> {
		return this.http.post<Paiement>(this.url + '/initier', demande);
	}

	consulter(reference: string): Observable<Paiement> {
		return this.http.get<Paiement>(`${this.url}/${encodeURIComponent(reference)}`);
	}
}
