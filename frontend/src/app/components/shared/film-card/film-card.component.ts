import { Component, computed, input, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FilmResume } from '../../../models/film.model';
import { formaterDuree } from '../../../services/format';
import { IconeComponent } from '../icone/icone.component';

@Component({
	selector: 'app-film-card',
	imports: [RouterLink, IconeComponent],
	templateUrl: './film-card.component.html',
	styleUrl: './film-card.component.scss',
})
export class FilmCardComponent {
	readonly film = input.required<FilmResume>();

	/** L'affiche apparaît en fondu une fois chargée. */
	protected readonly afficheChargee = signal(false);

	protected readonly meta = computed(() => {
		const f = this.film();
		return [f.annee, formaterDuree(f.dureeMinutes), f.genres[0]].filter(Boolean).join(' · ');
	});
}
