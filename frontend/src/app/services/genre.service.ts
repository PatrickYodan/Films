import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, shareReplay } from 'rxjs';
import { Genre } from '../models/genre.model';

@Injectable({
	providedIn: 'root',
})
export class GenreService {
	private readonly url = '/ws/genre';
	private readonly http = inject(HttpClient);

	/** Référentiel mis en cache pour la durée de la session. */
	private readonly liste$ = this.http.get<Genre[]>(this.url + '/lister').pipe(shareReplay(1));

	lister(): Observable<Genre[]> {
		return this.liste$;
	}
}
