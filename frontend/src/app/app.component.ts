import { Component, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NavigationCancel, NavigationEnd, NavigationError, Router, RouterOutlet } from '@angular/router';
import { filter, take } from 'rxjs';
import { FooterComponent } from './components/footer/footer.component';
import { HeaderComponent } from './components/header/header.component';
import { BarreChargementComponent } from './components/shared/barre-chargement/barre-chargement.component';
import { DialogueConfirmationComponent } from './components/shared/dialogue-confirmation/dialogue-confirmation.component';
import { IconeComponent } from './components/shared/icone/icone.component';
import { NomIcone } from './components/shared/icone/icones';
import { ApplicationMessageService, TypeMessage } from './services/message.service';
import { PreloaderService } from './services/preloader.service';

@Component({
	selector: 'app-root',
	imports: [RouterOutlet, HeaderComponent, FooterComponent, BarreChargementComponent, DialogueConfirmationComponent, IconeComponent],
	templateUrl: './app.component.html',
	styleUrl: './app.component.scss',
})
export class AppComponent {
	protected readonly messageService = inject(ApplicationMessageService);

	protected readonly icones: Record<TypeMessage, NomIcone> = {
		succes: 'succes',
		erreur: 'alerte',
		info: 'info',
	};

	constructor() {
		// L'écran de chargement (index.html) disparaît dès que la première page est affichée.
		const preloader = inject(PreloaderService);
		inject(Router)
			.events.pipe(
				filter((e) => e instanceof NavigationEnd || e instanceof NavigationCancel || e instanceof NavigationError),
				take(1),
				takeUntilDestroyed()
			)
			.subscribe(() => preloader.masquer());
	}
}
