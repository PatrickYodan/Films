import { Component, inject, input, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { messageErreur } from '../../models/application-erreur.model';
import { AuthService } from '../../services/auth.service';
import { IconeComponent } from '../shared/icone/icone.component';

@Component({
	selector: 'app-login',
	imports: [ReactiveFormsModule, RouterLink, IconeComponent],
	templateUrl: './login.component.html',
	styleUrl: './login.component.scss',
})
export class LoginComponent {
	/** Page à rouvrir après connexion (query param « retour »). */
	readonly retour = input<string | undefined>();

	private readonly authService = inject(AuthService);
	private readonly router = inject(Router);

	protected readonly formulaire = inject(FormBuilder).nonNullable.group({
		email: ['', [Validators.required, Validators.email]],
		motDePasse: ['', Validators.required],
	});
	protected readonly erreur = signal<string | null>(null);
	protected readonly envoiEnCours = signal(false);
	protected readonly motDePasseVisible = signal(false);

	connecter(): void {
		if (this.formulaire.invalid) {
			this.formulaire.markAllAsTouched();
			return;
		}
		this.envoiEnCours.set(true);
		this.erreur.set(null);
		this.authService.authentifier(this.formulaire.getRawValue()).subscribe({
			next: () => this.router.navigateByUrl(this.retour() || '/'),
			error: (err) => {
				this.envoiEnCours.set(false);
				this.erreur.set(messageErreur(err, 'Connexion impossible.'));
			},
		});
	}
}
