import { TypeContenu } from './film.model';
import { Qualite } from './qualite.model';

/** Types alignés sur les DTO du back-office (presentation/dto/admin). */

export type StatutFilm = 'BROUILLON' | 'PUBLIE';
export type Fonction = 'ACTEUR' | 'REALISATEUR' | 'SCENARISTE' | 'PRODUCTEUR';

export const STATUTS_FILM: { code: StatutFilm; libelle: string }[] = [
	{ code: 'BROUILLON', libelle: 'Brouillon' },
	{ code: 'PUBLIE', libelle: 'Publié' },
];

export const FONCTIONS: { code: Fonction; libelle: string }[] = [
	{ code: 'ACTEUR', libelle: 'Acteur / actrice' },
	{ code: 'REALISATEUR', libelle: 'Réalisation' },
	{ code: 'SCENARISTE', libelle: 'Scénario' },
	{ code: 'PRODUCTEUR', libelle: 'Production' },
];

export const CLASSIFICATIONS_AGE = ['Tous', '10+', '13+', '16+', '18+'];

/** Limites identiques au backend. */
export const TAILLE_MAX_IMAGE = 5 * 1024 * 1024;
export const TYPES_IMAGE = ['image/jpeg', 'image/png', 'image/webp'];
/** Lien vidéo : manifeste HLS (.m3u8) ou fichier MP4 (.mp4), relatif au CDN ou en http(s). Identique au backend. */
export const MOTIF_CHEMIN_HLS = /^(?!.*\.\.)(https?:\/\/\S+|[A-Za-z0-9][A-Za-z0-9._/-]*)\.(m3u8|mp4)$/;
/** Taille maximale d'un fichier vidéo envoyé (ngoma.video.taille_max côté backend). */
export const TAILLE_MAX_VIDEO = 4 * 1024 * 1024 * 1024;
export const MOTIF_URL_VTT = /^https?:\/\/\S+\.vtt(\?\S*)?$/;
export const MOTIF_LANGUE = /^[a-zA-Z]{2,3}(-[a-zA-Z]{2})?$/;
export const MOTIF_URL = /^https?:\/\/\S+$/;

/** Ligne du tableau des films (FilmAdminResumeDto). */
export interface FilmAdminResume {
	id: number;
	slug: string;
	titre: string;
	type: TypeContenu;
	annee: number | null;
	afficheUrl: string | null;
	pays: string[];
	genres: string[];
	statut: StatutFilm;
	modifieLe: string | null;
}

export interface Participation {
	nom: string;
	fonction: Fonction;
	role: string | null;
	ordre: number | null;
}

export interface FichierVideo {
	qualite: Qualite;
	cheminHls: string;
	tailleOctets: number | null;
}

export interface SousTitre {
	langue: string;
	libelle: string;
	fichierVttUrl: string;
}

/** Fiche complète (FilmAdminDetailDto). */
export interface FilmAdminDetail {
	id: number;
	slug: string;
	titre: string;
	type: TypeContenu;
	synopsisCourt: string | null;
	synopsis: string | null;
	annee: number | null;
	dureeMinutes: number | null;
	classificationAge: string | null;
	dateSortie: string | null;
	genreIds: number[];
	paysCodes: string[];
	gratuit: boolean;
	exclusivite: boolean;
	telechargeable: boolean;
	afficheUrl: string | null;
	bandeauUrl: string | null;
	bandeAnnonceUrl: string | null;
	distribution: Participation[];
	videos: FichierVideo[];
	sousTitres: SousTitre[];
	statut: StatutFilm;
	/** Ce qui empêche la publication (vide si le film est complet). */
	elementsManquants: string[];
	version: number;
	creeLe: string | null;
	modifieLe: string | null;
}

/** Données envoyées à la création / modification (FilmEditionDto). */
export interface FilmEdition {
	titre: string;
	type: TypeContenu;
	synopsisCourt: string | null;
	synopsis: string | null;
	annee: number | null;
	dureeMinutes: number | null;
	classificationAge: string | null;
	dateSortie: string | null;
	genreIds: number[];
	paysCodes: string[];
	gratuit: boolean;
	exclusivite: boolean;
	telechargeable: boolean;
	bandeAnnonceUrl: string | null;
	/** Obligatoire en modification (verrou optimiste), null à la création. */
	version: number | null;
}

export type TypeImage = 'affiche' | 'bandeau';
