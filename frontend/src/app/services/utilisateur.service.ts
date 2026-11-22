import { HttpClient } from '@angular/common/http';
import { Injectable, computed, effect, inject, signal, untracked } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { ChangementMotDePasse, ModificationProfil, Utilisateur } from '../models/utilisateur.model';
import { AuthService } from './auth.service';

/** Taille maximale acceptée pour une photo de profil (identique au backend). */
export const TAILLE_MAX_PHOTO = 2 * 1024 * 1024;
export const TYPES_PHOTO = ['image/jpeg', 'image/png', 'image/webp'];

/**
 * Profil et photo de l'utilisateur connecté, partagés entre l'en-tête et « Mon compte ».
 * Rechargés automatiquement à chaque connexion / déconnexion.
 */
@Injectable({
	providedIn: 'root',
})
export class UtilisateurService {
	private readonly url = '/ws/utilisateur/moi';
	private readonly http = inject(HttpClient);
	private readonly authService = inject(AuthService);

	readonly profil = signal<Utilisateur | null>(null);
	/** URL locale (blob) de la photo : l'API exige le jeton, une simple balise <img> ne suffit pas. */
	readonly photoUrl = signal<string | null>(null);
	/** Nom à afficher : profil à jour, sinon celui du jeton. */
	readonly nom = computed(() => this.profil()?.nom ?? this.authService.utilisateur()?.nom ?? '');

	constructor() {
		effect(() => {
			const connecte = this.authService.connecte();
			untracked(() => (connecte ? this.recharger() : this.vider()));
		});
	}

	recharger(): void {
		this.http.get<Utilisateur>(this.url).subscribe({
			next: (profil) => this.appliquer(profil),
			error: () => this.vider(),
		});
	}

	modifier(modification: ModificationProfil): Observable<Utilisateur> {
		return this.http.put<Utilisateur>(this.url, modification).pipe(tap((p) => this.profil.set(p)));
	}

	changerMotDePasse(changement: ChangementMotDePasse): Observable<void> {
		return this.http.put<void>(this.url + '/mot-de-passe', changement);
	}

	envoyerPhoto(fichier: File): Observable<Utilisateur> {
		const formulaire = new FormData();
		formulaire.append('fichier', fichier);
		return this.http.post<Utilisateur>(this.url + '/photo', formulaire).pipe(
			tap((p) => {
				this.profil.set(p);
				this.definirPhoto(URL.createObjectURL(fichier));
			})
		);
	}

	supprimerPhoto(): Observable<Utilisateur> {
		return this.http.delete<Utilisateur>(this.url + '/photo').pipe(
			tap((p) => {
				this.profil.set(p);
				this.definirPhoto(null);
			})
		);
	}

	private appliquer(profil: Utilisateur): void {
		this.profil.set(profil);
		if (!profil.photo) {
			this.definirPhoto(null);
			return;
		}
		this.http.get(this.url + '/photo', { responseType: 'blob' }).subscribe({
			next: (blob) => this.definirPhoto(URL.createObjectURL(blob)),
			error: () => this.definirPhoto(null),
		});
	}

	private vider(): void {
		this.profil.set(null);
		this.definirPhoto(null);
	}

	private definirPhoto(url: string | null): void {
		const ancienne = this.photoUrl();
		if (ancienne) {
			URL.revokeObjectURL(ancienne);
		}
		this.photoUrl.set(url);
	}
}
