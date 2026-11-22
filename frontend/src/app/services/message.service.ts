import { Injectable, signal } from '@angular/core';

export type TypeMessage = 'succes' | 'erreur' | 'info';

export interface MessageApplication {
	id: number;
	type: TypeMessage;
	texte: string;
}

/** Messages éphémères (toasts) affichés par AppComponent. */
@Injectable({
	providedIn: 'root',
})
export class ApplicationMessageService {
	private compteur = 0;
	readonly messages = signal<MessageApplication[]>([]);

	succes(texte: string): void {
		this.ajouter('succes', texte);
	}

	erreur(texte: string): void {
		this.ajouter('erreur', texte);
	}

	info(texte: string): void {
		this.ajouter('info', texte);
	}

	fermer(id: number): void {
		this.messages.update((liste) => liste.filter((m) => m.id !== id));
	}

	private ajouter(type: TypeMessage, texte: string): void {
		const id = ++this.compteur;
		this.messages.update((liste) => [...liste, { id, type, texte }]);
		setTimeout(() => this.fermer(id), 6000);
	}
}
