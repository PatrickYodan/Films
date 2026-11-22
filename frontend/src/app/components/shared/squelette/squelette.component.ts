import { Component, computed, input } from '@angular/core';

export type TypeSquelette = 'rail' | 'grille' | 'fiche' | 'tableau' | 'lignes' | 'hero';

/**
 * Silhouettes animées affichées pendant le chargement, à la forme du contenu attendu.
 */
@Component({
	selector: 'app-squelette',
	templateUrl: './squelette.component.html',
	styleUrl: './squelette.component.scss',
})
export class SqueletteComponent {
	readonly type = input<TypeSquelette>('lignes');
	readonly nombre = input(6);

	protected readonly elements = computed(() => Array.from({ length: this.nombre() }, (_, i) => i));
}
