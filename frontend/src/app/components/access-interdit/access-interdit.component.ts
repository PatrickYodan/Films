import { Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { IconeComponent } from '../shared/icone/icone.component';

/**
 * Page d'erreur : « Page introuvable » (route **) ou « Accès interdit » (route /acces-interdit, data.interdit).
 */
@Component({
	selector: 'app-access-interdit',
	imports: [RouterLink, IconeComponent],
	templateUrl: './access-interdit.component.html',
	styleUrl: './access-interdit.component.scss',
})
export class AccessInterditComponent {
	protected readonly interdit = inject(ActivatedRoute).snapshot.data['interdit'] === true;
}
