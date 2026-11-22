import { Injectable, computed, signal } from '@angular/core';

/** Compte les requêtes HTTP en cours pour la barre de chargement globale. */
@Injectable({
	providedIn: 'root',
})
export class ChargementService {
	private readonly enCours = signal(0);
	readonly actif = computed(() => this.enCours() > 0);

	demarrer(): void {
		this.enCours.update((n) => n + 1);
	}

	terminer(): void {
		this.enCours.update((n) => Math.max(0, n - 1));
	}
}
