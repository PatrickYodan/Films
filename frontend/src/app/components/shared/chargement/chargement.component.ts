import { Component, input } from '@angular/core';

/**
 * Chargement aux couleurs de NGOMA : des barres qui battent comme un tambour.
 */
@Component({
	selector: 'app-chargement',
	templateUrl: './chargement.component.html',
	styleUrl: './chargement.component.scss',
	host: { '[class.plein]': 'plein()' },
})
export class ChargementComponent {
	readonly texte = input('Chargement…');
	/** Occupe une zone haute (chargement de page). */
	readonly plein = input(false);
}
