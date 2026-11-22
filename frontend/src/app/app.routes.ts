import { Routes } from '@angular/router';
import { adminChildGuard, adminGuard } from './services/permission/admin-guard.service';
import { authGuard } from './services/permission/auth-guard.service';

export const routes: Routes = [
	{
		path: '',
		title: 'NGOMA — Films africains en streaming',
		loadComponent: () => import('./components/accueil/accueil.component').then((m) => m.AccueilComponent),
	},
	{
		path: 'catalogue',
		title: 'Catalogue — NGOMA',
		loadComponent: () => import('./components/catalogue/catalogue.component').then((m) => m.CatalogueComponent),
	},
	{
		path: 'gratuit',
		title: 'Films gratuits — NGOMA',
		data: { gratuit: true },
		loadComponent: () => import('./components/catalogue/catalogue.component').then((m) => m.CatalogueComponent),
	},
	{
		path: 'pays/:code',
		title: 'Films par pays — NGOMA',
		loadComponent: () => import('./components/catalogue/catalogue.component').then((m) => m.CatalogueComponent),
	},
	{
		path: 'film/:slug',
		title: 'Fiche film — NGOMA',
		loadComponent: () => import('./components/fiche-film/fiche-film.component').then((m) => m.FicheFilmComponent),
	},
	{
		path: 'regarder/:slug',
		title: 'Lecture — NGOMA',
		canActivate: [authGuard],
		loadComponent: () => import('./components/lecteur/lecteur.component').then((m) => m.LecteurComponent),
	},
	{
		path: 'abonnement',
		title: 'Abonnement — NGOMA',
		canActivate: [authGuard],
		loadComponent: () => import('./components/abonnement/abonnement.component').then((m) => m.AbonnementComponent),
	},
	{
		path: 'mon-compte',
		title: 'Mon compte — NGOMA',
		canActivate: [authGuard],
		loadComponent: () => import('./components/mon-compte/mon-compte.component').then((m) => m.MonCompteComponent),
	},
	{
		path: 'connexion',
		title: 'Connexion — NGOMA',
		loadComponent: () => import('./components/login/login.component').then((m) => m.LoginComponent),
	},
	{
		path: 'inscription',
		title: 'Créer un compte — NGOMA',
		loadComponent: () => import('./components/inscription/inscription.component').then((m) => m.InscriptionComponent),
	},
	{
		path: 'admin',
		canActivate: [adminGuard],
		canActivateChild: [adminChildGuard],
		loadComponent: () => import('./components/admin/admin-layout/admin-layout.component').then((m) => m.AdminLayoutComponent),
		children: [
			{ path: '', pathMatch: 'full', redirectTo: 'films' },
			{
				path: 'films',
				title: 'Films — Administration NGOMA',
				loadComponent: () => import('./components/admin/admin-films/admin-films.component').then((m) => m.AdminFilmsComponent),
			},
			{
				path: 'films/nouveau',
				title: 'Ajouter un film — Administration NGOMA',
				loadComponent: () =>
					import('./components/admin/admin-film-form/admin-film-form.component').then((m) => m.AdminFilmFormComponent),
			},
			{
				path: 'films/:id',
				title: 'Modifier un film — Administration NGOMA',
				loadComponent: () =>
					import('./components/admin/admin-film-form/admin-film-form.component').then((m) => m.AdminFilmFormComponent),
			},
		],
	},
	{
		path: 'acces-interdit',
		title: 'Accès interdit — NGOMA',
		data: { interdit: true },
		loadComponent: () =>
			import('./components/access-interdit/access-interdit.component').then((m) => m.AccessInterditComponent),
	},
	{
		path: '**',
		title: 'Page introuvable — NGOMA',
		loadComponent: () =>
			import('./components/access-interdit/access-interdit.component').then((m) => m.AccessInterditComponent),
	},
];
