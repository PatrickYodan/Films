import { Injectable } from '@angular/core';

/**
 * Durée minimale d'affichage du préchargeur, comptée depuis l'ouverture de la page : le temps de voir
 * l'écran s'allumer, les silhouettes marcher dans la savane, le logo et l'accroche apparaître. Un clic, un toucher ou une touche la raccourcit.
 */
export const DUREE_MIN_MS = 6000;
/** Durée maximale d'attente des polices avant de révéler la page (évite un saut de typographie). */
const ATTENTE_POLICES_MS = 1200;
/** Temps laissé à la barre pour se compléter avant le fondu de sortie. */
const FIN_BARRE_MS = 280;

/**
 * Pilote le préchargeur défini dans index.html (#ngoma-preloader) : il est affiché avant même
 * le démarrage d'Angular, puis retiré quand la première page est prête et la durée minimale écoulée.
 */
@Injectable({
	providedIn: 'root',
})
export class PreloaderService {
	private masque = false;

	/** Complète la barre, fait disparaître l'écran de chargement (zoom + fondu) puis le retire du DOM. */
	masquer(): void {
		if (this.masque) {
			return;
		}
		this.masque = true;
		const racine = document.documentElement;
		const preloader = document.getElementById('ngoma-preloader');
		if (!preloader) {
			racine.classList.remove('ngoma-chargement');
			return;
		}
		const polices = document.fonts?.ready ?? Promise.resolve();
		Promise.all([
			Promise.race([polices, attendre(ATTENTE_POLICES_MS)]),
			Promise.race([attendre(this.resteAAfficher()), passeParUtilisateur(preloader)]),
		]).then(() => {
			preloader.classList.add('ngp-termine');
			setTimeout(() => {
				preloader.classList.add('ngp-sortie');
				racine.classList.remove('ngoma-chargement');
				const retirer = () => preloader.remove();
				preloader.addEventListener('transitionend', retirer, { once: true });
				setTimeout(retirer, 1200);
			}, FIN_BARRE_MS);
		});
	}

	/** Temps restant avant la durée minimale (aucun si l'utilisateur a demandé moins d'animations). */
	private resteAAfficher(): number {
		if (typeof matchMedia !== 'undefined' && matchMedia('(prefers-reduced-motion: reduce)').matches) {
			return 0;
		}
		return Math.max(0, DUREE_MIN_MS - performance.now());
	}
}

function attendre(ms: number): Promise<void> {
	return new Promise((fin) => setTimeout(fin, ms));
}

/** Se résout au premier clic, toucher ou appui sur une touche : le préchargeur ne bloque jamais l'utilisateur. */
function passeParUtilisateur(preloader: HTMLElement): Promise<void> {
	return new Promise((fin) => {
		const passer = () => {
			preloader.removeEventListener('pointerdown', passer);
			document.removeEventListener('keydown', passer);
			fin();
		};
		preloader.addEventListener('pointerdown', passer);
		document.addEventListener('keydown', passer);
	});
}
