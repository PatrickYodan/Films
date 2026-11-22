import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { FilmDetail, FilmResume, RechercheFilm } from '../models/film.model';
import { Page } from '../models/page.model';

@Injectable({
	providedIn: 'root',
})
export class FilmService {
	private readonly url = '/ws/film';
	private readonly http = inject(HttpClient);

	/**
	 * Recherche dans le catalogue (du plus récent au plus ancien).
	 */
	lister(criteres: RechercheFilm = {}, page = 0, taille = 20): Observable<Page<FilmResume>> {
		let params = new HttpParams().set('page', page).set('taille', taille);
		for (const [cle, valeur] of Object.entries(criteres)) {
			if (valeur !== null && valeur !== undefined && valeur !== '') {
				params = params.set(cle, valeur);
			}
		}
		return this.http.get<Page<FilmResume>>(this.url + '/lister', { params });
	}

	detail(slug: string): Observable<FilmDetail> {
		return this.http.get<FilmDetail>(`${this.url}/${encodeURIComponent(slug)}`);
	}
}
