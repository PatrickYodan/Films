import { Qualite } from './qualite.model';

export interface Pass {
	id: number;
	code: string;
	nom: string;
	dureeHeures: number;
	prixFcfa: number;
	ecransMax: number;
	qualiteMax: Qualite;
	etiquette: string | null;
	description: string | null;
}
