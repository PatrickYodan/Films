export type Role = 'ABONNE' | 'ADMIN';

/** Utilisateur connecté, lu dans le jeton JWT. */
export interface UtilisateurConnecte {
	id: number;
	nom: string;
	email: string;
	role: Role;
}

/** Profil complet (GET /ws/utilisateur/moi). */
export interface Utilisateur {
	id: number;
	nom: string;
	email: string;
	telephone: string | null;
	role: Role;
	/** true si l'utilisateur a une photo de profil. */
	photo: boolean;
	inscritLe: string | null;
}

export interface ModificationProfil {
	nom: string;
	telephone: string | null;
}

export interface ChangementMotDePasse {
	motDePasseActuel: string;
	nouveauMotDePasse: string;
}

/** « Awa Kouassi Traoré » → « AK » */
export function initiales(nom: string | null | undefined): string {
	const mots = (nom ?? '').trim().split(/\s+/).filter(Boolean);
	if (!mots.length) {
		return '?';
	}
	const lettres = mots.length === 1 ? mots[0].slice(0, 2) : mots[0][0] + mots[1][0];
	return lettres.toUpperCase();
}
