import { Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, combineLatest, map, of, switchMap, tap } from 'rxjs';
import { messageErreur } from '../../models/application-erreur.model';
import { FilmResume, RechercheFilm } from '../../models/film.model';
import { Page } from '../../models/page.model';
import { FilmService } from '../../services/film.service';
import { GenreService } from '../../services/genre.service';
import { PaysService } from '../../services/pays.service';
import { FilmCardComponent } from '../shared/film-card/film-card.component';
import { IconeComponent } from '../shared/icone/icone.component';
import { SqueletteComponent } from '../shared/squelette/squelette.component';

const TAILLE_PAGE = 24;

/** Catalogue filtrable : /catalogue, /gratuit, /pays/:code ; les critères sont portés par l'URL. */
@Component({
	selector: 'app-catalogue',
	imports: [FilmCardComponent, IconeComponent, SqueletteComponent],
	templateUrl: './catalogue.component.html',
	styleUrl: './catalogue.component.scss',
})
export class CatalogueComponent {
	private readonly route = inject(ActivatedRoute);
	private readonly router = inject(Router);
	private readonly filmService = inject(FilmService);

	protected readonly genres = toSignal(inject(GenreService).lister().pipe(catchError(() => of([]))), {
		initialValue: [],
	});
	protected readonly pays = toSignal(inject(PaysService).lister().pipe(catchError(() => of([]))), { initialValue: [] });

	protected readonly criteres = signal<RechercheFilm>({});
	protected readonly page = signal(0);
	protected readonly resultat = signal<Page<FilmResume> | null>(null);
	protected readonly chargement = signal(true);
	protected readonly erreur = signal<string | null>(null);

	protected readonly titre = computed(() => {
		const c = this.criteres();
		if (c.motCle) {
			return `Résultats pour « ${c.motCle} »`;
		}
		if (c.paysCode) {
			return 'Films · ' + (this.pays().find((p) => p.code === c.paysCode)?.nom ?? c.paysCode);
		}
		return c.gratuit ? 'Films gratuits' : 'Catalogue';
	});
	protected readonly filtresActifs = computed(() => {
		const c = this.criteres();
		return !!(c.motCle || c.genreId || c.paysCode || c.gratuit);
	});

	constructor() {
		combineLatest([this.route.paramMap, this.route.queryParamMap, this.route.data])
			.pipe(
				map(([params, query, data]) => {
					const genreId = Number(query.get('genreId'));
					const criteres: RechercheFilm = {
						motCle: query.get('motCle'),
						genreId: genreId > 0 ? genreId : null,
						paysCode: params.get('code') ?? query.get('paysCode'),
						gratuit: data['gratuit'] === true || query.get('gratuit') === 'true' ? true : null,
					};
					return { criteres, page: Math.max(0, Number(query.get('page')) || 0) };
				}),
				tap(({ criteres, page }) => {
					this.criteres.set(criteres);
					this.page.set(page);
					this.erreur.set(null);
					this.chargement.set(true);
				}),
				switchMap(({ criteres, page }) =>
					this.filmService.lister(criteres, page, TAILLE_PAGE).pipe(
						catchError((err) => {
							this.erreur.set(messageErreur(err, 'Impossible de charger le catalogue.'));
							return of(null);
						})
					)
				),
				takeUntilDestroyed(inject(DestroyRef))
			)
			.subscribe((resultat) => {
				this.resultat.set(resultat);
				this.chargement.set(false);
			});
	}

	filtrer(changement: Partial<RechercheFilm>): void {
		const c = { ...this.criteres(), ...changement };
		this.router.navigate(['/catalogue'], {
			queryParams: {
				motCle: c.motCle || null,
				genreId: c.genreId || null,
				paysCode: c.paysCode || null,
				gratuit: c.gratuit ? true : null,
			},
		});
	}

	reinitialiser(): void {
		this.router.navigate(['/catalogue']);
	}

	allerPage(page: number): void {
		this.router.navigate([], { relativeTo: this.route, queryParams: { page: page || null }, queryParamsHandling: 'merge' });
		window.scrollTo({ top: 0, behavior: 'smooth' });
	}

	valeur(event: Event): string {
		return (event.target as HTMLInputElement | HTMLSelectElement).value;
	}
}
