import { Component, inject, input, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { messageErreur } from '../../models/application-erreur.model';
import { AuthService } from '../../services/auth.service';
import { IconeComponent } from '../shared/icone/icone.component';
import { CustomValidators } from '../../validators/custom-validators';

@Component({
	selector: 'app-inscription',
	imports: [ReactiveFormsModule, RouterLink, IconeComponent],
	templateUrl: './inscription.component.html',
	styleUrl: './inscription.component.scss',
})
export class InscriptionComponent {
	readonly retour = input<string | undefined>();

	private readonly authService = inject(AuthService);
	private readonly router = inject(Router);

	protected readonly formulaire = inject(FormBuilder).nonNullable.group(
		{
			nom: ['', [Validators.required, Validators.maxLength(150)]],
			email: ['', [Validators.required, Validators.email]],
			telephone: ['', CustomValidators.telephone()],
			motDePasse: ['', [Validators.required, Validators.minLength(8)]],
			confirmation: ['', Validators.required],
		},
		{ validators: CustomValidators.correspondance('motDePasse', 'confirmation') }
	);
	protected readonly erreur = signal<string | null>(null);
	protected readonly envoiEnCours = signal(false);
	protected readonly motDePasseVisible = signal(false);

	inscrire(): void {
		if (this.formulaire.invalid) {
			this.formulaire.markAllAsTouched();
			return;
		}
		const { nom, email, telephone, motDePasse } = this.formulaire.getRawValue();
		this.envoiEnCours.set(true);
		this.erreur.set(null);
		this.authService
			.inscrire({ nom, email, telephone: telephone.replace(/\s/g, '') || null, motDePasse })
			.subscribe({
				next: () => this.router.navigateByUrl(this.retour() || '/abonnement'),
				error: (err) => {
					this.envoiEnCours.set(false);
					this.erreur.set(messageErreur(err, 'Création du compte impossible.'));
				},
			});
	}
}
