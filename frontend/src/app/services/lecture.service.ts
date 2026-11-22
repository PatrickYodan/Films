import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Lecture } from '../models/lecture.model';
import { Qualite } from '../models/qualite.model';

@Injectable({
	providedIn: 'root',
})
export class LectureService {
	private readonly url = '/ws/lecture';
	private readonly http = inject(HttpClient);

	/**
	 * Ouvre la lecture : renvoie l'adresse du flux, ou une erreur 403 (pass nécessaire) / 404 (vidéo indisponible).
	 */
	ouvrir(slug: string, qualite: Qualite): Observable<Lecture> {
		const params = new HttpParams().set('qualite', qualite);
		return this.http.post<Lecture>(`${this.url}/${encodeURIComponent(slug)}`, null, { params });
	}
}
