import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, shareReplay } from 'rxjs';
import { Pays } from '../models/pays.model';

@Injectable({
	providedIn: 'root',
})
export class PaysService {
	private readonly url = '/ws/pays';
	private readonly http = inject(HttpClient);

	/** Référentiel mis en cache pour la durée de la session. */
	private readonly liste$ = this.http.get<Pays[]>(this.url + '/lister').pipe(shareReplay(1));

	lister(): Observable<Pays[]> {
		return this.liste$;
	}
}
