import { DatePipe } from '@angular/common';
import { Component, effect, inject, signal, untracked } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { catchError, of } from 'rxjs';
import { messageErreur } from '../../models/application-erreur.model';
import { AbonnementService } from '../../services/abonnement.service';
import { AuthService } from '../../services/auth.service';
import { ApplicationMessageService } from '../../services/message.service';
import { TAILLE_MAX_PHOTO, TYPES_PHOTO, UtilisateurService } from '../../services/utilisateur.service';
import { CustomValidators } from '../../validators/custom-validators';
import { AvatarComponent } from '../shared/avatar/avatar.component';
import { ChargementComponent } from '../shared/chargement/chargement.component';
import { IconeComponent } from '../shared/icone/icone.component';

@Component({
	selector: 'app-mon-compte',
	imports: [ReactiveFormsModule, RouterLink, DatePipe, AvatarComponent, ChargementComponent, IconeComponent],
	templateUrl: './mon-compte.component.html',
	styleUrl: './mon-compte.component.scss',
})
export class MonCompteComponent {
	protected readonly utilisateurService = inject(UtilisateurService);
	private readonly authService = inject(AuthService);
	private readonly messageService = inject(ApplicationMessageService);
	private readonly router = inject(Router);
	private readonly fb = inject(FormBuilder).nonNullable;

	protected readonly abonnement = toSignal(inject(AbonnementService).courant().pipe(catchError(() => of(null))), {
		initialValue: null,
	});

	protected readonly formulaireProfil = this.fb.group({
		nom: ['', [Validators.required, Validators.maxLength(150)]],
		telephone: ['', CustomValidators.telephone()],
	});
	protected readonly formulaireMotDePasse = this.fb.group(
		{
			motDePasseActuel: ['', Validators.required],
			nouveauMotDePasse: ['', [Validators.required, Validators.minLength(8)]],
			confirmation: ['', Validators.required],
		},
		{ validators: CustomValidators.correspondance('nouveauMotDePasse', 'confirmation') }
	);

	protected readonly erreurPhoto = signal<string | null>(null);
	protected readonly erreurProfil = signal<string | null>(null);
	protected readonly erreurMotDePasse = signal<string | null>(null);
	protected readonly enCours = signal<'photo' | 'profil' | 'mot-de-passe' | null>(null);
	protected readonly motsDePasseVisibles = signal(false);

	/** Nombre de jours (arrondi au supérieur) avant l'expiration du pass. */
	joursRestants(dateFin: string): number {
		return Math.max(0, Math.ceil((new Date(dateFin).getTime() - Date.now()) / 86_400_000));
	}

	constructor() {
		// Remplit le formulaire dès que le profil est chargé (sans écraser une saisie en cours).
		effect(() => {
			const profil = this.utilisateurService.profil();
			if (profil && this.formulaireProfil.pristine) {
				untracked(() => this.remplir(profil.nom, profil.telephone));
			}
		});
		this.utilisateurService.recharger();
	}

	remplir(nom: string, telephone: string | null): void {
		this.formulaireProfil.reset({ nom, telephone: telephone ?? '' });
	}

	choisirPhoto(event: Event): void {
		const champ = event.target as HTMLInputElement;
		const fichier = champ.files?.[0];
		champ.value = '';
		if (!fichier) {
			return;
		}
		if (!TYPES_PHOTO.includes(fichier.type) || fichier.size > TAILLE_MAX_PHOTO) {
			this.erreurPhoto.set('Choisissez une image JPEG, PNG ou WebP de 2 Mo maximum.');
			return;
		}
		this.erreurPhoto.set(null);
		this.enCours.set('photo');
		this.utilisateurService.envoyerPhoto(fichier).subscribe({
			next: () => {
				this.enCours.set(null);
				this.messageService.succes('Photo de profil mise à jour.');
			},
			error: (err) => {
				this.enCours.set(null);
				this.erreurPhoto.set(messageErreur(err, 'La photo n\'a pas pu être enregistrée.'));
			},
		});
	}

	retirerPhoto(): void {
		this.enCours.set('photo');
		this.utilisateurService.supprimerPhoto().subscribe({
			next: () => this.enCours.set(null),
			error: (err) => {
				this.enCours.set(null);
				this.erreurPhoto.set(messageErreur(err));
			},
		});
	}

	enregistrerProfil(): void {
		if (this.formulaireProfil.invalid) {
			this.formulaireProfil.markAllAsTouched();
			return;
		}
		const { nom, telephone } = this.formulaireProfil.getRawValue();
		this.erreurProfil.set(null);
		this.enCours.set('profil');
		this.utilisateurService.modifier({ nom, telephone: telephone.trim() || null }).subscribe({
			next: (p) => {
				this.enCours.set(null);
				this.remplir(p.nom, p.telephone);
				this.messageService.succes('Vos informations ont été enregistrées.');
			},
			error: (err) => {
				this.enCours.set(null);
				this.erreurProfil.set(messageErreur(err));
			},
		});
	}

	changerMotDePasse(): void {
		if (this.formulaireMotDePasse.invalid) {
			this.formulaireMotDePasse.markAllAsTouched();
			return;
		}
		const { motDePasseActuel, nouveauMotDePasse } = this.formulaireMotDePasse.getRawValue();
		this.erreurMotDePasse.set(null);
		this.enCours.set('mot-de-passe');
		this.utilisateurService.changerMotDePasse({ motDePasseActuel, nouveauMotDePasse }).subscribe({
			next: () => {
				this.enCours.set(null);
				this.formulaireMotDePasse.reset();
				this.messageService.succes('Mot de passe modifié.');
			},
			error: (err) => {
				this.enCours.set(null);
				this.erreurMotDePasse.set(messageErreur(err));
			},
		});
	}

	deconnecter(): void {
		this.authService.deconnecter();
		this.router.navigateByUrl('/');
	}
}
