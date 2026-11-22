import { Component, ElementRef, input, signal, viewChild } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FilmResume } from '../../../models/film.model';
import { FilmCardComponent } from '../film-card/film-card.component';
import { IconeComponent } from '../icone/icone.component';
import { SqueletteComponent } from '../squelette/squelette.component';

/** Rangée horizontale de films, avec flèches de défilement et lien « Tout voir ». */
@Component({
	selector: 'app-film-rail',
	imports: [RouterLink, FilmCardComponent, IconeComponent, SqueletteComponent],
	templateUrl: './film-rail.component.html',
	styleUrl: './film-rail.component.scss',
})
export class FilmRailComponent {
	readonly titre = input.required<string>();
	readonly films = input<FilmResume[]>([]);
	/** Affiche un squelette tant que les films ne sont pas chargés. */
	readonly chargement = input(false);
	readonly lienToutVoir = input<string | null>(null);
	readonly paramsToutVoir = input<Record<string, string | number | boolean> | null>(null);

	private readonly liste = viewChild<ElementRef<HTMLElement>>('liste');
	protected readonly debut = signal(true);
	protected readonly fin = signal(false);

	defiler(sens: 1 | -1): void {
		const element = this.liste()?.nativeElement;
		element?.scrollBy({ left: sens * element.clientWidth * 0.85, behavior: 'smooth' });
	}

	majPosition(): void {
		const element = this.liste()?.nativeElement;
		if (element) {
			this.debut.set(element.scrollLeft <= 4);
			this.fin.set(element.scrollLeft + element.clientWidth >= element.scrollWidth - 4);
		}
	}
}
