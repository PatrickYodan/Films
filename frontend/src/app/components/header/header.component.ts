import { Component, ElementRef, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { NavigationEnd, Router, RouterLink, RouterLinkActive } from '@angular/router';
import { filter } from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { UtilisateurService } from '../../services/utilisateur.service';
import { AvatarComponent } from '../shared/avatar/avatar.component';
import { IconeComponent } from '../shared/icone/icone.component';

@Component({
	selector: 'app-header',
	imports: [RouterLink, RouterLinkActive, FormsModule, AvatarComponent, IconeComponent],
	templateUrl: './header.component.html',
	styleUrl: './header.component.scss',
	host: {
		'(document:click)': 'clicDocument($event)',
		'(document:keydown.escape)': 'fermerMenus()',
	},
})
export class HeaderComponent {
	protected readonly authService = inject(AuthService);
	protected readonly utilisateurService = inject(UtilisateurService);
	private readonly router = inject(Router);
	private readonly element = inject(ElementRef<HTMLElement>);

	protected recherche = '';
	protected readonly menuOuvert = signal(false);
	protected readonly menuCompteOuvert = signal(false);
	protected readonly estAdmin = computed(() => this.authService.utilisateur()?.role === 'ADMIN');

	constructor() {
		this.router.events
			.pipe(
				filter((e) => e instanceof NavigationEnd),
				takeUntilDestroyed()
			)
			.subscribe(() => this.fermerMenus());
	}

	rechercher(): void {
		const motCle = this.recherche.trim();
		this.router.navigate(['/catalogue'], { queryParams: motCle ? { motCle } : {} });
		this.fermerMenus();
	}

	basculerMenuCompte(): void {
		this.menuCompteOuvert.update((ouvert) => !ouvert);
	}

	deconnecter(): void {
		this.authService.deconnecter();
		this.fermerMenus();
		this.router.navigateByUrl('/');
	}

	fermerMenus(): void {
		this.menuOuvert.set(false);
		this.menuCompteOuvert.set(false);
	}

	/** Ferme le menu du compte lors d'un clic ailleurs. */
	clicDocument(event: MouseEvent): void {
		const compte = this.element.nativeElement.querySelector('.compte');
		if (this.menuCompteOuvert() && compte && !compte.contains(event.target as Node)) {
			this.menuCompteOuvert.set(false);
		}
	}
}
