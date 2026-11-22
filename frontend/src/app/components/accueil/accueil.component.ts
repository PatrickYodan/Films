import { Component, computed, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { Observable, catchError, map, of } from 'rxjs';
import { FilmResume, RechercheFilm } from '../../models/film.model';
import { MOYENS_PAIEMENT } from '../../models/paiement.model';
import { Pass } from '../../models/pass.model';
import { Pays } from '../../models/pays.model';
import { FilmService } from '../../services/film.service';
import { formaterFcfa } from '../../services/format';
import { PassService } from '../../services/pass.service';
import { PaysService } from '../../services/pays.service';
import { FilmRailComponent } from '../shared/film-rail/film-rail.component';
import { HeroCarrouselComponent } from './hero-carrousel/hero-carrousel.component';
import { IconeComponent } from '../shared/icone/icone.component';
import { NomIcone } from '../shared/icone/icones';
import { SqueletteComponent } from '../shared/squelette/squelette.component';

/** Pays mis en avant sur l'accueil (rail « Films · … »). */
const PAYS_EN_AVANT = 'CI';
/** Nombre de films du carrousel « À la une ». */
const NOMBRE_VEDETTES = 5;

@Component({
	selector: 'app-accueil',
	imports: [RouterLink, FilmRailComponent, HeroCarrouselComponent, IconeComponent, SqueletteComponent],
	templateUrl: './accueil.component.html',
	styleUrl: './accueil.component.scss',
})
export class AccueilComponent {
	private readonly filmService = inject(FilmService);

	// « undefined » = en cours de chargement (squelettes)
	protected readonly nouveautes = toSignal(this.films({}));
	protected readonly gratuits = toSignal(this.films({ gratuit: true }));
	protected readonly filmsPaysEnAvant = toSignal(this.films({ paysCode: PAYS_EN_AVANT }));
	protected readonly pays = toSignal(inject(PaysService).lister().pipe(catchError(() => of([] as Pays[]))));
	protected readonly passList = toSignal(inject(PassService).lister().pipe(catchError(() => of([] as Pass[]))), {
		initialValue: [],
	});

	/**
	 * Films mis en avant dans le carrousel : exclusivités d'abord, puis les plus récents (5 au plus).
	 * undefined pendant le chargement, tableau vide s'il n'y a aucun film.
	 */
	protected readonly vedettes = computed(() => {
		const films = this.nouveautes();
		if (films === undefined) {
			return undefined;
		}
		return [...films.filter((f) => f.exclusivite), ...films.filter((f) => !f.exclusivite)].slice(0, NOMBRE_VEDETTES);
	});
	protected readonly passMoinsCher = computed(
		() => [...this.passList()].sort((a, b) => a.prixFcfa - b.prixFcfa)[0] ?? null
	);
	protected readonly titrePaysEnAvant = computed(
		() => 'Films · ' + (this.pays()?.find((p) => p.code === PAYS_EN_AVANT)?.nom ?? 'Côte d’Ivoire')
	);

	protected readonly moyens = MOYENS_PAIEMENT;
	protected readonly formaterFcfa = formaterFcfa;
	protected readonly attentePays = Array.from({ length: 7 }, (_, i) => i);
	protected readonly atouts: { icone: NomIcone; titre: string; texte: string }[] = [
		{ icone: 'feuille', titre: 'Économie de données', texte: 'Regardez en 240p avec moins de 150 Mo par heure.' },
		{ icone: 'telephone', titre: 'Paiement Mobile Money', texte: 'Orange Money, MTN MoMo, Wave, Moov Money ou carte.' },
		{ icone: 'ticket', titre: 'Sans engagement', texte: 'Un pass pour un jour, une semaine ou un mois.' },
		{ icone: 'sous-titres', titre: 'Sous-titres FR / EN', texte: 'Des films de tout le continent, sous-titrés.' },
	];

	private films(criteres: RechercheFilm): Observable<FilmResume[]> {
		return this.filmService.lister(criteres, 0, 12).pipe(
			map((page) => page.contenu),
			catchError(() => of([]))
		);
	}
}
