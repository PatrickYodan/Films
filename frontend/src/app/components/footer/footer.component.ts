import { Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { environment } from '../../../environments/environment';
import { MOYENS_PAIEMENT } from '../../models/paiement.model';
import { AuthService } from '../../services/auth.service';
import { IconeComponent } from '../shared/icone/icone.component';
import { NomIcone } from '../shared/icone/icones';

interface ReseauSocial {
	nom: string;
	icone: NomIcone;
	url: string;
}

@Component({
	selector: 'app-footer',
	imports: [RouterLink, IconeComponent],
	templateUrl: './footer.component.html',
	styleUrl: './footer.component.scss',
})
export class FooterComponent {
	protected readonly authService = inject(AuthService);
	protected readonly annee = new Date().getFullYear();
	protected readonly moyens = MOYENS_PAIEMENT;
	protected readonly estAdmin = computed(() => this.authService.utilisateur()?.role === 'ADMIN');

	/** Réseaux renseignés dans environment.reseauxSociaux (les adresses vides sont ignorées). */
	protected readonly reseaux: ReseauSocial[] = (
		[
			{ nom: 'WhatsApp', icone: 'whatsapp', url: environment.reseauxSociaux.whatsapp },
			{ nom: 'YouTube', icone: 'youtube', url: environment.reseauxSociaux.youtube },
			{ nom: 'Facebook', icone: 'facebook', url: environment.reseauxSociaux.facebook },
		] satisfies ReseauSocial[]
	).filter((r) => r.url);
}
