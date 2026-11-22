import { Pass } from './pass.model';

export type StatutAbonnement = 'ACTIF' | 'EXPIRE' | 'ANNULE';

export interface Abonnement {
	id: number;
	pass: Pass;
	dateDebut: string;
	dateFin: string;
	statut: StatutAbonnement;
}
