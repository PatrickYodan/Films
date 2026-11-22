import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { IconeComponent } from '../../shared/icone/icone.component';
import { NomIcone } from '../../shared/icone/icones';

/** Cadre du back-office : barre latérale + contenu. */
@Component({
	selector: 'app-admin-layout',
	imports: [RouterOutlet, RouterLink, RouterLinkActive, IconeComponent],
	templateUrl: './admin-layout.component.html',
	styleUrl: './admin-layout.component.scss',
})
export class AdminLayoutComponent {
	/** Modules à venir, affichés désactivés. */
	protected readonly prochainement: { libelle: string; icone: NomIcone }[] = [
		{ libelle: 'Pass', icone: 'ticket' },
		{ libelle: 'Paiements', icone: 'portefeuille' },
		{ libelle: 'Utilisateurs', icone: 'utilisateurs' },
	];
}
