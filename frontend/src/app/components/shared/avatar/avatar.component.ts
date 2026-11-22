import { Component, computed, input } from '@angular/core';
import { initiales } from '../../../models/utilisateur.model';

/**
 * Avatar rond : la photo si elle existe, sinon les initiales du nom sur le fond par défaut.
 */
@Component({
	selector: 'app-avatar',
	templateUrl: './avatar.component.html',
	styleUrl: './avatar.component.scss',
	host: {
		'[class.grand]': "taille() === 'grand'",
		'[attr.title]': 'nom()',
	},
})
export class AvatarComponent {
	readonly nom = input<string | null | undefined>('');
	readonly photoUrl = input<string | null | undefined>(null);
	readonly taille = input<'petit' | 'grand'>('petit');

	protected readonly initiales = computed(() => initiales(this.nom()));
}
