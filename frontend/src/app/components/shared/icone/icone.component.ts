import { Component, computed, input } from '@angular/core';
import { ICONES, NomIcone } from './icones';

/**
 * Icône du jeu NGOMA (SVG au trait, couleur héritée du texte). Décorative par défaut :
 * le texte accessible doit être porté par l'élément parent (libellé ou aria-label).
 */
@Component({
	selector: 'app-icone',
	templateUrl: './icone.component.html',
	styleUrl: './icone.component.scss',
})
export class IconeComponent {
	readonly nom = input.required<NomIcone>();
	readonly taille = input(20);
	readonly epaisseur = input(1.75);

	protected readonly chemins = computed(() => ICONES[this.nom()]);
}
