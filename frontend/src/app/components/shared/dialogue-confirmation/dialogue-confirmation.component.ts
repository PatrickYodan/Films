import { Component, ElementRef, effect, inject, viewChild } from '@angular/core';
import { ConfirmationService } from '../../../services/confirmation.service';
import { IconeComponent } from '../icone/icone.component';

/** Fenêtre modale de confirmation, pilotée par ConfirmationService (une seule instance, dans AppComponent). */
@Component({
	selector: 'app-dialogue-confirmation',
	imports: [IconeComponent],
	templateUrl: './dialogue-confirmation.component.html',
	styleUrl: './dialogue-confirmation.component.scss',
})
export class DialogueConfirmationComponent {
	protected readonly service = inject(ConfirmationService);
	private readonly dialogue = viewChild.required<ElementRef<HTMLDialogElement>>('dialogue');

	constructor() {
		effect(() => {
			const element = this.dialogue().nativeElement;
			if (this.service.demande() && !element.open) {
				element.showModal();
			} else if (!this.service.demande() && element.open) {
				element.close();
			}
		});
	}

	repondre(confirme: boolean): void {
		this.service.repondre(confirme);
	}

	/** Un clic sur le fond (hors du contenu) annule. */
	clicFond(event: MouseEvent): void {
		if (event.target === this.dialogue().nativeElement) {
			this.repondre(false);
		}
	}
}
