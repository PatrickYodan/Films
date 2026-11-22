import { DatePipe } from '@angular/common';
import { Component, DestroyRef, computed, inject, input, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Subscription, catchError, filter, of, switchMap, take, takeWhile, timer } from 'rxjs';
import { Abonnement } from '../../models/abonnement.model';
import { messageErreur } from '../../models/application-erreur.model';
import { MOYENS_PAIEMENT, MoyenPaiement, Paiement } from '../../models/paiement.model';
import { Pass } from '../../models/pass.model';
import { QUALITES } from '../../models/qualite.model';
import { AbonnementService } from '../../services/abonnement.service';
import { formaterDureePass, formaterFcfa } from '../../services/format';
import { PaiementService } from '../../services/paiement.service';
import { PassService } from '../../services/pass.service';
import { CustomValidators } from '../../validators/custom-validators';
import { IconeComponent } from '../shared/icone/icone.component';

type Etape = 'saisie' | 'attente' | 'confirme' | 'echec';

/** Intervalle et nombre maximal d'interrogations du statut (3 s × 60 = 3 min). */
const INTERVALLE_SUIVI_MS = 3000;
const SUIVIS_MAX = 60;

@Component({
	selector: 'app-abonnement',
	imports: [ReactiveFormsModule, RouterLink, DatePipe, IconeComponent],
	templateUrl: './abonnement.component.html',
	styleUrl: './abonnement.component.scss',
})
export class AbonnementComponent {
	/** Page à rouvrir après le paiement (ex. /regarder/lagos-express), passée par le lecteur. */
	readonly retour = input<string | undefined>();
	/** Seuls les chemins internes sont acceptés (pas de redirection vers un autre site). */
	protected readonly retourSur = computed(() => {
		const r = this.retour();
		return r && r.startsWith('/') && !r.startsWith('//') ? r : null;
	});

	private readonly passService = inject(PassService);
	private readonly paiementService = inject(PaiementService);
	private readonly abonnementService = inject(AbonnementService);
	private readonly destroyRef = inject(DestroyRef);
	private suivi?: Subscription;

	/** undefined pendant le chargement. */
	protected readonly passList = toSignal(this.passService.lister().pipe(catchError(() => of([] as Pass[]))));
	protected readonly abonnement = signal<Abonnement | null>(null);

	protected readonly codePass = signal<string | null>(null);
	protected readonly passChoisi = computed(() => {
		const liste = this.passList() ?? [];
		return (
			liste.find((p) => p.code === this.codePass()) ??
			liste.find((p) => p.etiquette === 'Le plus choisi') ??
			liste[0] ??
			null
		);
	});

	protected readonly moyens = MOYENS_PAIEMENT;
	protected readonly moyen = signal<MoyenPaiement>('ORANGE_MONEY');
	protected readonly moyenChoisi = computed(() => this.moyens.find((m) => m.code === this.moyen())!);
	protected readonly telephone = new FormControl('+225 ', { nonNullable: true, validators: [CustomValidators.telephone()] });

	protected readonly etape = signal<Etape>('saisie');
	protected readonly paiement = signal<Paiement | null>(null);
	protected readonly erreur = signal<string | null>(null);
	protected readonly envoiEnCours = signal(false);

	protected readonly formaterFcfa = formaterFcfa;
	protected readonly etapes = ['Choix du pass', 'Paiement', 'Confirmation'];
	protected readonly attentePass = [0, 1, 2];
	/** Étape mise en avant dans l'indicateur de progression. */
	protected readonly etapeIndex = computed(() => {
		switch (this.etape()) {
			case 'attente':
			case 'echec':
				return 1;
			case 'confirme':
				return 3;
			default:
				return 0;
		}
	});
	protected readonly formaterDureePass = formaterDureePass;

	constructor() {
		this.chargerAbonnement();
	}

	libelleQualite(pass: Pass): string {
		return QUALITES.find((q) => q.code === pass.qualiteMax)?.libelle ?? '';
	}

	payer(): void {
		const pass = this.passChoisi();
		if (!pass) {
			return;
		}
		const numero = this.telephone.value.replace(/\s/g, '');
		if (this.moyenChoisi().mobileMoney && (numero.length < 9 || this.telephone.invalid)) {
			this.telephone.markAsTouched();
			this.erreur.set('Saisissez le numéro Mobile Money qui va payer.');
			return;
		}
		this.erreur.set(null);
		this.envoiEnCours.set(true);
		this.paiementService
			.initier({ codePass: pass.code, moyen: this.moyen(), telephone: this.moyenChoisi().mobileMoney ? numero : null })
			.subscribe({
				next: (paiement) => {
					if (paiement.urlPaiement) {
						// Carte, Wave… : la validation se fait sur la page de l'opérateur.
						window.location.assign(paiement.urlPaiement);
						return;
					}
					this.envoiEnCours.set(false);
					this.paiement.set(paiement);
					this.etape.set('attente');
					this.suivre(paiement.reference);
				},
				error: (err) => {
					this.envoiEnCours.set(false);
					this.erreur.set(messageErreur(err, 'Le paiement n\'a pas pu être lancé.'));
				},
			});
	}

	recommencer(): void {
		this.suivi?.unsubscribe();
		this.paiement.set(null);
		this.erreur.set(null);
		this.etape.set('saisie');
	}

	/** Interroge le statut du paiement jusqu'à confirmation, échec ou délai dépassé. */
	private suivre(reference: string): void {
		this.suivi?.unsubscribe();
		this.suivi = timer(INTERVALLE_SUIVI_MS, INTERVALLE_SUIVI_MS)
			.pipe(
				take(SUIVIS_MAX),
				switchMap(() => this.paiementService.consulter(reference).pipe(catchError(() => of(null)))),
				filter((p): p is Paiement => p !== null),
				takeWhile((p) => p.statut === 'EN_ATTENTE', true),
				takeUntilDestroyed(this.destroyRef)
			)
			.subscribe({
				next: (p) => {
					this.paiement.set(p);
					if (p.statut === 'CONFIRME') {
						// L'écran de confirmation s'affiche avec la nouvelle date de fin (pass prolongé).
						this.chargerAbonnement(() => this.etape.set('confirme'));
					} else if (p.statut !== 'EN_ATTENTE') {
						this.etape.set('echec');
					}
				},
				complete: () => {
					if (this.etape() === 'attente') {
						this.erreur.set('Nous n\'avons pas encore reçu la confirmation. Si vous avez validé, votre pass sera activé dans quelques minutes.');
						this.etape.set('echec');
					}
				},
			});
	}

	private chargerAbonnement(ensuite?: () => void): void {
		this.abonnementService
			.courant()
			.pipe(catchError(() => of(null)))
			.subscribe((a) => {
				this.abonnement.set(a);
				ensuite?.();
			});
	}
}
