import { Genre } from './genre.model';
import { Pays } from './pays.model';

export type TypeContenu = 'FILM' | 'SERIE';

/** Carte film (rails, catalogue). */
export interface FilmResume {
	id: number;
	slug: string;
	titre: string;
	synopsisCourt: string | null;
	annee: number | null;
	dureeMinutes: number | null;
	classificationAge: string | null;
	afficheUrl: string | null;
	bandeauUrl: string | null;
	gratuit: boolean;
	exclusivite: boolean;
	genres: string[];
	pays: string[];
}

/** Fiche film complète. */
export interface FilmDetail {
	id: number;
	slug: string;
	titre: string;
	synopsisCourt: string | null;
	synopsis: string | null;
	annee: number | null;
	dureeMinutes: number | null;
	classificationAge: string | null;
	type: TypeContenu;
	gratuit: boolean;
	exclusivite: boolean;
	telechargeable: boolean;
	afficheUrl: string | null;
	bandeauUrl: string | null;
	bandeAnnonceUrl: string | null;
	dateSortie: string | null;
	genres: Genre[];
	pays: Pays[];
}

/** Critères de recherche du catalogue. */
export interface RechercheFilm {
	motCle?: string | null;
	genreId?: number | null;
	paysCode?: string | null;
	gratuit?: boolean | null;
}
