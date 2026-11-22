import { Injectable, signal } from '@angular/core';

export interface DemandeConfirmation {
	titre: string;
	message: string;
	libelleConfirmer?: string;
	/** Action destructrice : bouton rouge, focus sur « Annuler ». */
	danger?: boolean;
}

interface DemandeEnCours extends DemandeConfirmation {
	resoudre: (confirme: boolean) => void;
}

/**
 * Boîte de confirmation de l'application (remplace window.confirm).
 * Usage : `if (await confirmation.confirmer({ titre, message, danger: true })) { … }`
 */
@Injectable({
	providedIn: 'root',
})
export class ConfirmationService {
	readonly demande = signal<DemandeEnCours | null>(null);

	confirmer(demande: DemandeConfirmation): Promise<boolean> {
		this.demande()?.resoudre(false);
		return new Promise((resoudre) => this.demande.set({ ...demande, resoudre }));
	}

	repondre(confirme: boolean): void {
		const demande = this.demande();
		this.demande.set(null);
		demande?.resoudre(confirme);
	}
}
