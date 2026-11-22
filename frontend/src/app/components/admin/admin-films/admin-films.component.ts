import { DatePipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { Observable, catchError, of, switchMap, tap } from 'rxjs';
import { FilmAdminResume, STATUTS_FILM, StatutFilm } from '../../../models/admin-film.model';
import { messageErreur } from '../../../models/application-erreur.model';
import { Page } from '../../../models/page.model';
import { AdminFilmService } from '../../../services/admin-film.service';
import { ConfirmationService } from '../../../services/confirmation.service';
import { ApplicationMessageService } from '../../../services/message.service';
import { IconeComponent } from '../../shared/icone/icone.component';
import { SqueletteComponent } from '../../shared/squelette/squelette.component';

const TAILLE_PAGE = 20;

@Component({
	selector: 'app-admin-films',
	imports: [RouterLink, DatePipe, IconeComponent, SqueletteComponent],
	templateUrl: './admin-films.component.html',
	styleUrl: './admin-films.component.scss',
})
export class AdminFilmsComponent {
	private readonly adminFilmService = inject(AdminFilmService);
	private readonly messageService = inject(ApplicationMessageService);
	private readonly confirmation = inject(ConfirmationService);

	protected readonly statuts = STATUTS_FILM;
	protected readonly motCle = signal('');
	protected readonly statut = signal<StatutFilm | null>(null);
	protected readonly page = signal(0);
	/** Incrémenté pour recharger la liste après une action. */
	private readonly rechargement = signal(0);
	protected readonly actionEnCours = signal<number | null>(null);
	protected readonly erreur = signal<string | null>(null);
	protected readonly chargement = signal(true);

	private readonly criteres = computed(() => ({
		motCle: this.motCle(),
		statut: this.statut(),
		page: this.page(),
		rechargement: this.rechargement(),
	}));

	protected readonly resultat = toSignal(
		toObservable(this.criteres).pipe(
			tap(() => {
				this.erreur.set(null);
				this.chargement.set(true);
			}),
			switchMap((c) =>
				this.adminFilmService.lister(c.motCle, c.statut, c.page, TAILLE_PAGE).pipe(
					catchError((err) => {
						this.erreur.set(messageErreur(err, 'Impossible de charger les films.'));
						return of(null as Page<FilmAdminResume> | null);
					})
				)
			),
			tap(() => this.chargement.set(false))
		),
		{ initialValue: null }
	);

	rechercher(valeur: string): void {
		this.motCle.set(valeur.trim());
		this.page.set(0);
	}

	filtrerStatut(valeur: string): void {
		this.statut.set((valeur || null) as StatutFilm | null);
		this.page.set(0);
	}

	publier(film: FilmAdminResume): void {
		this.executer(film, this.adminFilmService.publier(film.id), `« ${film.titre} » est publié.`);
	}

	depublier(film: FilmAdminResume): void {
		this.executer(film, this.adminFilmService.depublier(film.id), `« ${film.titre} » est retiré du catalogue.`);
	}

	async supprimer(film: FilmAdminResume): Promise<void> {
		const confirme = await this.confirmation.confirmer({
			titre: 'Supprimer ce film ?',
			message: `« ${film.titre} » sera supprimé définitivement, avec ses images, sa distribution et l'historique de visionnage. Pour le retirer temporairement du catalogue, préférez « Dépublier ».`,
			libelleConfirmer: 'Supprimer',
			danger: true,
		});
		if (!confirme) {
			return;
		}
		this.executer(film, this.adminFilmService.supprimer(film.id), `« ${film.titre} » a été supprimé.`);
	}

	valeur(event: Event): string {
		return (event.target as HTMLInputElement | HTMLSelectElement).value;
	}

	private executer(film: FilmAdminResume, action: Observable<unknown>, succes: string): void {
		this.actionEnCours.set(film.id);
		action.subscribe({
			next: () => {
				this.actionEnCours.set(null);
				this.messageService.succes(succes);
				this.rechargement.update((n) => n + 1);
			},
			error: (err) => {
				this.actionEnCours.set(null);
				this.messageService.erreur(messageErreur(err));
			},
		});
	}
}
