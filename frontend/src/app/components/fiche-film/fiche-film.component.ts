import { Component, computed, inject, input, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { Router, RouterLink } from '@angular/router';
import { catchError, map, of, switchMap, tap } from 'rxjs';
import { messageErreur } from '../../models/application-erreur.model';
import { FilmDetail } from '../../models/film.model';
import { QUALITES, Qualite } from '../../models/qualite.model';
import { FilmService } from '../../services/film.service';
import { formaterDuree } from '../../services/format';
import { FilmRailComponent } from '../shared/film-rail/film-rail.component';
import { IconeComponent } from '../shared/icone/icone.component';
import { SqueletteComponent } from '../shared/squelette/squelette.component';

export const CLE_QUALITE = 'ngoma_qualite';

@Component({
	selector: 'app-fiche-film',
	imports: [RouterLink, FilmRailComponent, IconeComponent, SqueletteComponent],
	templateUrl: './fiche-film.component.html',
	styleUrl: './fiche-film.component.scss',
})
export class FicheFilmComponent {
	/** Paramètre de route :slug (withComponentInputBinding). */
	readonly slug = input.required<string>();

	private readonly filmService = inject(FilmService);
	private readonly router = inject(Router);

	protected readonly erreur = signal<string | null>(null);
	protected readonly film = toSignal(
		toObservable(this.slug).pipe(
			tap(() => this.erreur.set(null)),
			switchMap((slug) =>
				this.filmService.detail(slug).pipe(
					catchError((err) => {
						this.erreur.set(messageErreur(err, 'Ce film est introuvable.'));
						return of(null);
					})
				)
			)
		),
		{ initialValue: null }
	);

	protected readonly similaires = toSignal(
		toObservable(this.film).pipe(
			switchMap((film) =>
				film?.genres.length
					? this.filmService.lister({ genreId: film.genres[0].id }, 0, 13).pipe(
							map((page) => page.contenu.filter((f) => f.id !== film.id).slice(0, 12)),
							catchError(() => of([]))
						)
					: of([])
			)
		),
		{ initialValue: [] }
	);

	protected readonly duree = computed(() => formaterDuree(this.film()?.dureeMinutes));
	protected readonly bandeauCharge = signal(false);
	protected readonly afficheChargee = signal(false);

	protected readonly qualites = QUALITES;
	protected readonly qualite = signal<Qualite>((localStorage.getItem(CLE_QUALITE) as Qualite) ?? 'STANDARD_480P');

	choisirQualite(qualite: Qualite): void {
		this.qualite.set(qualite);
		localStorage.setItem(CLE_QUALITE, qualite);
	}

	regarder(film: FilmDetail): void {
		this.router.navigate(['/regarder', film.slug], { queryParams: { qualite: this.qualite() } });
	}
}
