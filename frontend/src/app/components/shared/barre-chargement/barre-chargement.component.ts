import { Component, inject } from '@angular/core';
import { ChargementService } from '../../../services/chargement.service';

/** Fine barre animée en haut de page pendant les appels HTTP. */
@Component({
	selector: 'app-barre-chargement',
	templateUrl: './barre-chargement.component.html',
	styleUrl: './barre-chargement.component.scss',
})
export class BarreChargementComponent {
	protected readonly chargementService = inject(ChargementService);
}
