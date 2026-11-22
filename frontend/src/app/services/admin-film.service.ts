import { HttpClient, HttpEvent, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import {
	FichierVideo,
	FilmAdminDetail,
	FilmAdminResume,
	FilmEdition,
	Participation,
	SousTitre,
	StatutFilm,
	TypeImage,
} from '../models/admin-film.model';
import { Page } from '../models/page.model';
import { Qualite } from '../models/qualite.model';

/** API du back-office des films (réservée aux administrateurs). */
@Injectable({
	providedIn: 'root',
})
export class AdminFilmService {
	private readonly url = '/ws/admin/film';
	private readonly http = inject(HttpClient);

	lister(motCle: string | null, statut: StatutFilm | null, page = 0, taille = 20): Observable<Page<FilmAdminResume>> {
		let params = new HttpParams().set('page', page).set('taille', taille);
		if (motCle?.trim()) {
			params = params.set('motCle', motCle.trim());
		}
		if (statut) {
			params = params.set('statut', statut);
		}
		return this.http.get<Page<FilmAdminResume>>(this.url + '/lister', { params });
	}

	detail(id: number): Observable<FilmAdminDetail> {
		return this.http.get<FilmAdminDetail>(`${this.url}/${id}`);
	}

	creer(edition: FilmEdition): Observable<FilmAdminDetail> {
		return this.http.post<FilmAdminDetail>(this.url, edition);
	}

	modifier(id: number, edition: FilmEdition): Observable<FilmAdminDetail> {
		return this.http.put<FilmAdminDetail>(`${this.url}/${id}`, edition);
	}

	supprimer(id: number): Observable<void> {
		return this.http.delete<void>(`${this.url}/${id}`);
	}

	publier(id: number): Observable<FilmAdminDetail> {
		return this.http.post<FilmAdminDetail>(`${this.url}/${id}/publier`, null);
	}

	depublier(id: number): Observable<FilmAdminDetail> {
		return this.http.post<FilmAdminDetail>(`${this.url}/${id}/depublier`, null);
	}

	/**
	 * Envoie l'affiche ou le bandeau ; émet les événements de progression puis la réponse.
	 */
	envoyerImage(id: number, type: TypeImage, fichier: File): Observable<HttpEvent<FilmAdminDetail>> {
		const formulaire = new FormData();
		formulaire.append('fichier', fichier);
		return this.http.post<FilmAdminDetail>(`${this.url}/${id}/${type}`, formulaire, {
			reportProgress: true,
			observe: 'events',
		});
	}

	/**
	 * Envoie un fichier vidéo MP4 pour une qualité (corps brut, lu en flux par le backend) ;
	 * émet la progression puis la fiche mise à jour. Se désabonner annule l'envoi.
	 */
	envoyerVideo(id: number, qualite: Qualite, fichier: File): Observable<HttpEvent<FilmAdminDetail>> {
		return this.http.put<FilmAdminDetail>(`${this.url}/${id}/videos/${qualite}/fichier`, fichier, {
			headers: { 'Content-Type': 'application/octet-stream' },
			reportProgress: true,
			observe: 'events',
		});
	}

	/** Envoie un fichier de sous-titres (.vtt ou .srt, converti par le serveur) pour une langue. */
	envoyerSousTitre(id: number, langue: string, libelle: string, fichier: File): Observable<FilmAdminDetail> {
		const formulaire = new FormData();
		formulaire.append('langue', langue);
		formulaire.append('libelle', libelle);
		formulaire.append('fichier', fichier);
		return this.http.post<FilmAdminDetail>(`${this.url}/${id}/sous-titres/fichier`, formulaire);
	}

	remplacerDistribution(id: number, lignes: Participation[]): Observable<FilmAdminDetail> {
		return this.http.put<FilmAdminDetail>(`${this.url}/${id}/distribution`, lignes);
	}

	remplacerVideos(id: number, lignes: FichierVideo[]): Observable<FilmAdminDetail> {
		return this.http.put<FilmAdminDetail>(`${this.url}/${id}/videos`, lignes);
	}

	remplacerSousTitres(id: number, lignes: SousTitre[]): Observable<FilmAdminDetail> {
		return this.http.put<FilmAdminDetail>(`${this.url}/${id}/sous-titres`, lignes);
	}
}
