import { Component, computed, input, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FilmResume } from '../../../models/film.model';
import { formaterDuree } from '../../../services/format';
import { IconeComponent } from '../../shared/icone/icone.component';
import { NomIcone } from '../../shared/icone/icones';

/** Durée d'affichage de chaque film, en millisecondes (synchronisée avec la jauge CSS). */
export const DUREE_DIAPO_MS = 6000;

/**
 * Carrousel « À la une » de l'accueil : met en avant plusieurs films à tour de rôle.
 * Le défilement est piloté par l'animation de la jauge (fin d'animation = film suivant) :
 * pause au survol, au clavier, quand l'onglet est caché ou sur demande ; désactivé si
 * l'utilisateur a demandé à réduire les animations. Balayage tactile sur mobile.
 */
@Component({
	selector: 'app-hero-carrousel',
	imports: [RouterLink, IconeComponent],
	templateUrl: './hero-carrousel.component.html',
	styleUrl: './hero-carrousel.component.scss',
	host: {
		'(document:visibilitychange)': 'majVisibilite()',
		'[style.--duree-diapo]': "dureeDiapo + 'ms'",
	},
})
export class HeroCarrouselComponent {
	readonly films = input.required<FilmResume[]>();

	protected readonly dureeDiapo = DUREE_DIAPO_MS;
	protected readonly index = signal(0);
	protected readonly survol = signal(false);
	protected readonly focus = signal(false);
	protected readonly pauseManuelle = signal(false);
	protected readonly ongletCache = signal(false);
	protected readonly imagesChargees = signal<ReadonlySet<number>>(new Set());

	/** Pas de défilement automatique si le système demande de réduire les animations. */
	protected readonly defilementAuto =
		typeof matchMedia === 'undefined' || !matchMedia('(prefers-reduced-motion: reduce)').matches;
	protected readonly enPause = computed(
		() => this.survol() || this.focus() || this.pauseManuelle() || this.ongletCache()
	);
	protected readonly actif = computed(() => this.films()[this.index()] ?? this.films()[0]);
	/** Tableau d'un seul film : recréé à chaque changement, ce qui rejoue les animations d'entrée du texte. */
	protected readonly diapo = computed(() => [this.actif()]);
	protected readonly meta = computed(() => {
		const f = this.actif();
		const meta: { icone: NomIcone; texte: string }[] = [];
		if (f.annee) {
			meta.push({ icone: 'calendrier', texte: String(f.annee) });
		}
		if (f.dureeMinutes) {
			meta.push({ icone: 'horloge', texte: formaterDuree(f.dureeMinutes) });
		}
		if (f.genres.length) {
			meta.push({ icone: 'film', texte: f.genres.join(', ') });
		}
		return meta;
	});

	private debutGlisser: number | null = null;

	libelle(film: FilmResume): string {
		if (film.exclusivite) {
			return 'À la une · Exclusivité';
		}
		return film.gratuit ? 'À la une · Gratuit' : 'À la une';
	}

	suivant(): void {
		const total = this.films().length;
		this.index.update((i) => (i + 1) % total);
	}

	precedent(): void {
		const total = this.films().length;
		this.index.update((i) => (i - 1 + total) % total);
	}

	aller(index: number): void {
		this.index.set(index);
	}

	/**
	 * Place d'une affiche dans la pile : 0 = film à la une, 1 et 2 = suivants en éventail,
	 * « sortie » = film précédent qui s'efface, « cache » = films plus loin.
	 */
	position(i: number): string {
		const total = this.films().length;
		const ecart = (i - this.index() + total) % total;
		if (ecart <= 2) {
			return String(ecart);
		}
		return ecart === total - 1 ? 'sortie' : 'cache';
	}

	/** Onglet caché : le défilement s'arrête (on ne fait pas défiler des films que personne ne voit). */
	majVisibilite(): void {
		this.ongletCache.set(document.hidden);
	}

	imageChargee(id: number): void {
		this.imagesChargees.update((ids) => new Set(ids).add(id));
	}

	commencerGlisser(event: PointerEvent): void {
		this.debutGlisser = event.pointerType === 'mouse' ? null : event.clientX;
	}

	terminerGlisser(event: PointerEvent): void {
		if (this.debutGlisser === null) {
			return;
		}
		const ecart = event.clientX - this.debutGlisser;
		this.debutGlisser = null;
		if (Math.abs(ecart) > 50) {
			if (ecart < 0) {
				this.suivant();
			} else {
				this.precedent();
			}
		}
	}
}
