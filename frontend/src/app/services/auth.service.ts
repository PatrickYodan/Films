import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { Auth, Inscription } from '../models/auth.model';
import { Token } from '../models/token.model';
import { Utilisateur, UtilisateurConnecte } from '../models/utilisateur.model';

export const CLE_TOKEN = 'access_token';

interface ContenuJwt {
	sub: string;
	id: number;
	nom: string;
	role: UtilisateurConnecte['role'];
	exp: number;
}

/** Décode la partie « payload » d'un JWT (sans vérifier la signature, faite par le backend). */
export function decoderToken(jwt: string): ContenuJwt | null {
	try {
		const base64 = jwt.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
		const octets = Uint8Array.from(atob(base64), (c) => c.charCodeAt(0));
		return JSON.parse(new TextDecoder().decode(octets)) as ContenuJwt;
	} catch {
		return null;
	}
}

export function tokenExpire(jwt: string): boolean {
	const contenu = decoderToken(jwt);
	return !contenu || contenu.exp * 1000 <= Date.now();
}

@Injectable({
	providedIn: 'root',
})
export class AuthService {
	private readonly url = '/ws/securite';
	private readonly http = inject(HttpClient);

	readonly utilisateur = signal<UtilisateurConnecte | null>(this.lireUtilisateur());
	readonly connecte = computed(() => this.utilisateur() !== null);

	/**
	 * Authentifie l'utilisateur et conserve son jeton.
	 */
	authentifier(auth: Auth): Observable<Token> {
		return this.http.post<Token>(this.url + '/auth', auth).pipe(tap((t) => this.enregistrer(t.token)));
	}

	/**
	 * Crée un compte abonné et le connecte.
	 */
	inscrire(inscription: Inscription): Observable<Token> {
		return this.http.post<Token>(this.url + '/inscription', inscription).pipe(tap((t) => this.enregistrer(t.token)));
	}

	profil(): Observable<Utilisateur> {
		return this.http.get<Utilisateur>('/ws/utilisateur/moi');
	}

	deconnecter(): void {
		localStorage.removeItem(CLE_TOKEN);
		this.utilisateur.set(null);
	}

	/** @return le jeton s'il est encore valide. */
	getToken(): string | null {
		const token = localStorage.getItem(CLE_TOKEN);
		return token && !tokenExpire(token) ? token : null;
	}

	isAuthenticated(): boolean {
		return this.getToken() !== null;
	}

	private enregistrer(token: string): void {
		localStorage.setItem(CLE_TOKEN, token);
		this.utilisateur.set(this.lireUtilisateur());
	}

	private lireUtilisateur(): UtilisateurConnecte | null {
		const token = this.getToken();
		const contenu = token ? decoderToken(token) : null;
		return contenu ? { id: contenu.id, nom: contenu.nom, email: contenu.sub, role: contenu.role } : null;
	}
}
