import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, shareReplay } from 'rxjs';
import { Pass } from '../models/pass.model';

@Injectable({
	providedIn: 'root',
})
export class PassService {
	private readonly url = '/ws/pass';
	private readonly http = inject(HttpClient);

	/** Référentiel mis en cache pour la durée de la session. */
	private readonly liste$ = this.http.get<Pass[]>(this.url + '/lister').pipe(shareReplay(1));

	lister(): Observable<Pass[]> {
		return this.liste$;
	}
}
