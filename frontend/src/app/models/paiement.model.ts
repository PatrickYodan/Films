export type MoyenPaiement = 'ORANGE_MONEY' | 'MTN_MOMO' | 'WAVE' | 'MOOV_MONEY' | 'CARTE';
export type StatutPaiement = 'EN_ATTENTE' | 'CONFIRME' | 'ECHOUE' | 'EXPIRE';

export interface OptionMoyenPaiement {
	code: MoyenPaiement;
	libelle: string;
	mobileMoney: boolean;
	/** Couleur repère de l'opérateur (pastille), pas un logo. */
	couleur: string;
}

/** Moyens de paiement de la maquette « Abonnement & paiement ». */
export const MOYENS_PAIEMENT: OptionMoyenPaiement[] = [
	{ code: 'ORANGE_MONEY', libelle: 'Orange Money', mobileMoney: true, couleur: '#ff7900' },
	{ code: 'MTN_MOMO', libelle: 'MTN MoMo', mobileMoney: true, couleur: '#ffcb05' },
	{ code: 'WAVE', libelle: 'Wave', mobileMoney: true, couleur: '#1dc8f2' },
	{ code: 'MOOV_MONEY', libelle: 'Moov Money', mobileMoney: true, couleur: '#2f80ed' },
	{ code: 'CARTE', libelle: 'Carte bancaire', mobileMoney: false, couleur: '#b3a99c' },
];

export interface InitierPaiement {
	codePass: string;
	moyen: MoyenPaiement;
	telephone: string | null;
}

export interface Paiement {
	reference: string;
	codePass: string;
	moyen: MoyenPaiement;
	telephone: string | null;
	montantFcfa: number;
	statut: StatutPaiement;
	creeLe: string;
	confirmeLe: string | null;
	/** Page de l'opérateur où rediriger l'utilisateur (carte, Wave…), sinon null. */
	urlPaiement: string | null;
	/** true si l'opérateur est simulé : aucun argent réel ne circule. */
	modeTest: boolean;
}
