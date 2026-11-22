export type Qualite = 'ECO_240P' | 'STANDARD_480P' | 'HD_1080P';

export interface OptionQualite {
	code: Qualite;
	libelle: string;
	description: string;
	/** 1 à 3 : nombre de barres de l'indicateur. */
	niveau: 1 | 2 | 3;
}

/** Qualités de lecture proposées (maquette « Qualité de lecture »). */
export const QUALITES: OptionQualite[] = [
	{ code: 'ECO_240P', libelle: 'Économie · 240p', description: 'Idéal en 3G, consomme peu de données.', niveau: 1 },
	{ code: 'STANDARD_480P', libelle: 'Standard · 480p', description: 'Bon équilibre image / données.', niveau: 2 },
	{ code: 'HD_1080P', libelle: 'HD · 1080p', description: 'Meilleure image, en Wi-Fi ou 4G.', niveau: 3 },
];
