import { HttpErrorResponse, HttpEventType } from '@angular/common/http';
import { Component, computed, effect, inject, input, signal, untracked } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormControl, FormGroup, NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { Observable, Subscription, catchError, of, switchMap, tap } from 'rxjs';
import {
	CLASSIFICATIONS_AGE,
	FONCTIONS,
	FichierVideo,
	FilmAdminDetail,
	FilmEdition,
	Fonction,
	MOTIF_CHEMIN_HLS,
	MOTIF_LANGUE,
	MOTIF_URL,
	MOTIF_URL_VTT,
	Participation,
	SousTitre,
	TAILLE_MAX_IMAGE,
	TAILLE_MAX_VIDEO,
	TYPES_IMAGE,
	TypeImage,
} from '../../../models/admin-film.model';
import { messageErreur } from '../../../models/application-erreur.model';
import { TypeContenu } from '../../../models/film.model';
import { QUALITES, Qualite } from '../../../models/qualite.model';
import { AdminFilmService } from '../../../services/admin-film.service';
import { GenreService } from '../../../services/genre.service';
import { ApplicationMessageService } from '../../../services/message.service';
import { PaysService } from '../../../services/pays.service';
import { CustomValidators } from '../../../validators/custom-validators';
import { ChargementComponent } from '../../shared/chargement/chargement.component';
import { IconeComponent } from '../../shared/icone/icone.component';

type LigneDistribution = FormGroup<{
	nom: FormControl<string>;
	fonction: FormControl<Fonction>;
	role: FormControl<string>;
}>;
type LigneSousTitre = FormGroup<{
	langue: FormControl<string>;
	libelle: FormControl<string>;
	fichierVttUrl: FormControl<string>;
}>;

/** Fabrique du formulaire, exportée pour les tests de validation. */
export function creerFormulaireFilm(fb: NonNullableFormBuilder) {
	return fb.group({
		titre: ['', [Validators.required, Validators.maxLength(255)]],
		type: fb.control<TypeContenu>('FILM', Validators.required),
		synopsisCourt: ['', Validators.maxLength(500)],
		synopsis: ['', Validators.maxLength(5000)],
		annee: fb.control<number | null>(null, [Validators.min(1900), Validators.max(2100)]),
		dureeMinutes: fb.control<number | null>(null, [Validators.min(1), Validators.max(1000)]),
		classificationAge: [''],
		dateSortie: [''],
		genreIds: fb.control<number[]>([], CustomValidators.auMoinsUn()),
		paysCodes: fb.control<string[]>([], CustomValidators.auMoinsUn()),
		gratuit: [false],
		exclusivite: [false],
		telechargeable: [false],
		bandeAnnonceUrl: ['', [Validators.maxLength(500), Validators.pattern(MOTIF_URL)]],
		distribution: fb.array<LigneDistribution>([]),
		videos: fb.group({
			ECO_240P: ['', Validators.pattern(MOTIF_CHEMIN_HLS)],
			STANDARD_480P: ['', Validators.pattern(MOTIF_CHEMIN_HLS)],
			HD_1080P: ['', Validators.pattern(MOTIF_CHEMIN_HLS)],
		}),
		sousTitres: fb.array<LigneSousTitre>([]),
	});
}

/**
 * Création (/admin/films/nouveau) et modification (/admin/films/:id) d'un film.
 * La fiche est enregistrée d'abord, puis la distribution, les vidéos et les sous-titres.
 */
@Component({
	selector: 'app-admin-film-form',
	imports: [ReactiveFormsModule, RouterLink, ChargementComponent, IconeComponent],
	templateUrl: './admin-film-form.component.html',
	styleUrl: './admin-film-form.component.scss',
})
export class AdminFilmFormComponent {
	/** Paramètre de route :id (absent en création). */
	readonly id = input<string>();

	private readonly fb = inject(NonNullableFormBuilder);
	private readonly adminFilmService = inject(AdminFilmService);
	private readonly messageService = inject(ApplicationMessageService);
	private readonly router = inject(Router);

	protected readonly genres = toSignal(inject(GenreService).lister().pipe(catchError(() => of([]))), { initialValue: [] });
	protected readonly pays = toSignal(inject(PaysService).lister().pipe(catchError(() => of([]))), { initialValue: [] });

	protected readonly formulaire = creerFormulaireFilm(this.fb);
	protected readonly film = signal<FilmAdminDetail | null>(null);
	protected readonly chargement = signal(false);
	protected readonly enCours = signal(false);
	protected readonly erreur = signal<string | null>(null);
	/** true après un 409 : le film a été modifié ailleurs. */
	protected readonly conflit = signal(false);
	protected readonly progression = signal<Record<TypeImage, number | null>>({ affiche: null, bandeau: null });
	protected readonly erreurImage = signal<Record<TypeImage, string | null>>({ affiche: null, bandeau: null });
	/** Zone d'image survolée pendant un glisser-déposer. */
	protected readonly survol = signal<TypeImage | null>(null);
	/** Envoi de fichiers vidéo : progression (%) et erreur par qualité, zone survolée en glisser-déposer. */
	protected readonly envoisVideo = signal<Partial<Record<Qualite, number>>>({});
	protected readonly erreursVideo = signal<Partial<Record<Qualite, string>>>({});
	protected readonly survolVideo = signal<Qualite | null>(null);
	private readonly abonnementsVideo: Partial<Record<Qualite, Subscription>> = {};
	/** Envoi de fichiers de sous-titres : ligne en cours d'envoi et erreurs par ligne. */
	protected readonly envoiSousTitre = signal<number | null>(null);
	protected readonly erreursSousTitre = signal<Record<number, string>>({});

	protected readonly creation = computed(() => !this.id());
	protected readonly publie = computed(() => this.film()?.statut === 'PUBLIE');
	protected readonly images = computed(() => {
		const f = this.film();
		return [
			{ type: 'affiche' as TypeImage, titre: 'Affiche (portrait 2:3)', url: f?.afficheUrl ?? null },
			{ type: 'bandeau' as TypeImage, titre: 'Bandeau (paysage 16:9)', url: f?.bandeauUrl ?? null },
		];
	});

	protected readonly qualites = QUALITES;
	protected readonly fonctions = FONCTIONS;
	protected readonly classifications = CLASSIFICATIONS_AGE;
	/** Tailles connues des renditions, renvoyées telles quelles (non saisies dans le formulaire). */
	private tailles: Partial<Record<Qualite, number | null>> = {};

	constructor() {
		effect(() => {
			const id = this.id();
			untracked(() => (id ? this.charger(Number(id)) : null));
		});
	}

	get distribution() {
		return this.formulaire.controls.distribution;
	}

	get sousTitres() {
		return this.formulaire.controls.sousTitres;
	}

	// ------------------------------------------------------------------ chargement

	charger(id: number): void {
		this.chargement.set(true);
		this.erreur.set(null);
		this.conflit.set(false);
		this.adminFilmService.detail(id).subscribe({
			next: (film) => {
				this.chargement.set(false);
				this.remplir(film);
			},
			error: (err) => {
				this.chargement.set(false);
				this.erreur.set(messageErreur(err, 'Film introuvable.'));
			},
		});
	}

	private remplir(film: FilmAdminDetail): void {
		this.film.set(film);
		this.distribution.clear();
		film.distribution.forEach((p) => this.distribution.push(this.ligneDistribution(p)));
		this.sousTitres.clear();
		film.sousTitres.forEach((s) => this.sousTitres.push(this.ligneSousTitre(s)));
		const chemin = (q: Qualite) => film.videos.find((v) => v.qualite === q)?.cheminHls ?? '';
		this.tailles = Object.fromEntries(film.videos.map((v) => [v.qualite, v.tailleOctets]));
		this.formulaire.reset({
			titre: film.titre,
			type: film.type,
			synopsisCourt: film.synopsisCourt ?? '',
			synopsis: film.synopsis ?? '',
			annee: film.annee,
			dureeMinutes: film.dureeMinutes,
			classificationAge: film.classificationAge ?? '',
			dateSortie: film.dateSortie ?? '',
			genreIds: film.genreIds,
			paysCodes: film.paysCodes,
			gratuit: film.gratuit,
			exclusivite: film.exclusivite,
			telechargeable: film.telechargeable,
			bandeAnnonceUrl: film.bandeAnnonceUrl ?? '',
			videos: { ECO_240P: chemin('ECO_240P'), STANDARD_480P: chemin('STANDARD_480P'), HD_1080P: chemin('HD_1080P') },
		});
	}

	// ------------------------------------------------------------------ lignes dynamiques

	ligneDistribution(p?: Participation): LigneDistribution {
		return this.fb.group({
			nom: [p?.nom ?? '', [Validators.required, Validators.maxLength(150)]],
			fonction: this.fb.control<Fonction>(p?.fonction ?? 'ACTEUR', Validators.required),
			role: [p?.role ?? '', Validators.maxLength(150)],
		});
	}

	ligneSousTitre(s?: SousTitre): LigneSousTitre {
		return this.fb.group({
			langue: [s?.langue ?? '', [Validators.required, Validators.pattern(MOTIF_LANGUE)]],
			libelle: [s?.libelle ?? '', [Validators.required, Validators.maxLength(50)]],
			fichierVttUrl: [s?.fichierVttUrl ?? '', [Validators.required, Validators.maxLength(500), Validators.pattern(MOTIF_URL_VTT)]],
		});
	}

	ajouterDistribution(): void {
		this.distribution.push(this.ligneDistribution());
		this.distribution.markAsDirty();
	}

	retirerDistribution(index: number): void {
		this.distribution.removeAt(index);
		this.distribution.markAsDirty();
	}

	ajouterSousTitre(): void {
		this.sousTitres.push(this.ligneSousTitre());
		this.sousTitres.markAsDirty();
	}

	retirerSousTitre(index: number): void {
		this.sousTitres.removeAt(index);
		this.sousTitres.markAsDirty();
	}

	basculerGenre(id: number): void {
		this.basculer(this.formulaire.controls.genreIds, id);
	}

	basculerPays(code: string): void {
		this.basculer(this.formulaire.controls.paysCodes, code);
	}

	private basculer<T>(controle: FormControl<T[]>, valeur: T): void {
		const actuelles = controle.value;
		controle.setValue(actuelles.includes(valeur) ? actuelles.filter((v) => v !== valeur) : [...actuelles, valeur]);
		controle.markAsDirty();
		controle.markAsTouched();
	}

	// ------------------------------------------------------------------ enregistrement

	enregistrer(): void {
		this.soumettre((film) => of(film), 'Brouillon enregistré.');
	}

	publier(): void {
		this.soumettre((film) => this.adminFilmService.publier(film.id), 'Film publié dans le catalogue.');
	}

	depublier(): void {
		const film = this.film();
		if (!film) {
			return;
		}
		this.enCours.set(true);
		this.adminFilmService.depublier(film.id).subscribe({
			next: (f) => {
				this.enCours.set(false);
				this.film.set(f);
				this.messageService.succes('Film retiré du catalogue.');
			},
			error: (err) => this.gererErreur(err),
		});
	}

	/**
	 * Enregistre la fiche puis les compositions, et enchaîne éventuellement une action (publication).
	 */
	private soumettre(suite: (film: FilmAdminDetail) => Observable<FilmAdminDetail>, succes: string): void {
		if (this.formulaire.invalid) {
			this.formulaire.markAllAsTouched();
			this.erreur.set('Le formulaire contient des erreurs : vérifiez les champs signalés en rouge.');
			return;
		}
		const creation = !this.film();
		this.enCours.set(true);
		this.erreur.set(null);
		this.sauvegarder()
			.pipe(switchMap((film) => suite(film)))
			.subscribe({
				next: (film) => {
					this.enCours.set(false);
					this.remplir(film);
					this.messageService.succes(succes);
					if (creation) {
						this.router.navigate(['/admin/films', film.id], { replaceUrl: true });
					}
				},
				error: (err) => this.gererErreur(err),
			});
	}

	private sauvegarder(): Observable<FilmAdminDetail> {
		const film = this.film();
		const fiche$ = film ? this.adminFilmService.modifier(film.id, this.edition()) : this.adminFilmService.creer(this.edition());
		return fiche$.pipe(
			// Mémorise tout de suite le film créé : un nouvel essai ne créera pas de doublon.
			tap((f) => this.film.set(f)),
			switchMap((f) => this.adminFilmService.remplacerDistribution(f.id, this.lignesDistribution())),
			switchMap((f) => this.adminFilmService.remplacerVideos(f.id, this.lignesVideos())),
			switchMap((f) => this.adminFilmService.remplacerSousTitres(f.id, this.lignesSousTitres())),
			tap((f) => this.film.set(f))
		);
	}

	edition(): FilmEdition {
		const v = this.formulaire.getRawValue();
		const texte = (s: string) => s.trim() || null;
		return {
			titre: v.titre.trim(),
			type: v.type,
			synopsisCourt: texte(v.synopsisCourt),
			synopsis: texte(v.synopsis),
			annee: v.annee,
			dureeMinutes: v.dureeMinutes,
			classificationAge: v.classificationAge || null,
			dateSortie: v.dateSortie || null,
			genreIds: v.genreIds,
			paysCodes: v.paysCodes,
			gratuit: v.gratuit,
			exclusivite: v.exclusivite,
			telechargeable: v.telechargeable,
			bandeAnnonceUrl: texte(v.bandeAnnonceUrl),
			version: this.film()?.version ?? null,
		};
	}

	private lignesDistribution(): Participation[] {
		return this.distribution.getRawValue().map((l, ordre) => ({
			nom: l.nom.trim(),
			fonction: l.fonction,
			role: l.role.trim() || null,
			ordre,
		}));
	}

	private lignesVideos(): FichierVideo[] {
		const videos = this.formulaire.controls.videos.getRawValue();
		return (Object.keys(videos) as Qualite[])
			.filter((q) => videos[q].trim())
			.map((q) => ({ qualite: q, cheminHls: videos[q].trim(), tailleOctets: this.tailles[q] ?? null }));
	}

	private lignesSousTitres(): SousTitre[] {
		return this.sousTitres.getRawValue().map((s) => ({
			langue: s.langue.trim(),
			libelle: s.libelle.trim(),
			fichierVttUrl: s.fichierVttUrl.trim(),
		}));
	}

	private gererErreur(err: unknown): void {
		this.enCours.set(false);
		if (err instanceof HttpErrorResponse && err.status === 409) {
			this.conflit.set(true);
			this.erreur.set(
				'Ce film a été modifié entre-temps (par un collègue ou dans un autre onglet). ' +
					'Rechargez-le pour récupérer la dernière version : vos modifications non enregistrées seront perdues.'
			);
			return;
		}
		this.erreur.set(messageErreur(err));
	}

	recharger(): void {
		const film = this.film();
		if (film) {
			this.charger(film.id);
		}
	}

	// ------------------------------------------------------------------ images

	envoyerImage(type: TypeImage, event: Event): void {
		const champ = event.target as HTMLInputElement;
		const fichier = champ.files?.[0];
		champ.value = '';
		this.televerser(type, fichier);
	}

	deposer(type: TypeImage, event: DragEvent): void {
		event.preventDefault();
		this.survol.set(null);
		this.televerser(type, event.dataTransfer?.files?.[0]);
	}

	private televerser(type: TypeImage, fichier: File | undefined): void {
		const film = this.film();
		if (!fichier || !film) {
			return;
		}
		if (!TYPES_IMAGE.includes(fichier.type) || fichier.size > TAILLE_MAX_IMAGE) {
			this.erreurImage.update((e) => ({ ...e, [type]: 'Choisissez une image JPEG, PNG ou WebP de 5 Mo maximum.' }));
			return;
		}
		this.erreurImage.update((e) => ({ ...e, [type]: null }));
		this.progression.update((p) => ({ ...p, [type]: 0 }));
		this.adminFilmService.envoyerImage(film.id, type, fichier).subscribe({
			next: (evenement) => {
				if (evenement.type === HttpEventType.UploadProgress && evenement.total) {
					const pourcentage = Math.round((100 * evenement.loaded) / evenement.total);
					this.progression.update((p) => ({ ...p, [type]: pourcentage }));
				} else if (evenement.type === HttpEventType.Response && evenement.body) {
					// Met à jour l'image et la version sans écraser les saisies en cours du formulaire.
					this.film.set(evenement.body);
					this.progression.update((p) => ({ ...p, [type]: null }));
					this.messageService.succes(type === 'affiche' ? 'Affiche enregistrée.' : 'Bandeau enregistré.');
				}
			},
			error: (err) => {
				this.progression.update((p) => ({ ...p, [type]: null }));
				this.erreurImage.update((e) => ({ ...e, [type]: messageErreur(err) }));
			},
		});
	}

	// ------------------------------------------------------------------ fichiers vidéo

	choisirVideo(qualite: Qualite, event: Event): void {
		const champ = event.target as HTMLInputElement;
		const fichier = champ.files?.[0];
		champ.value = '';
		this.envoyerVideo(qualite, fichier);
	}

	deposerVideo(qualite: Qualite, event: DragEvent): void {
		event.preventDefault();
		this.survolVideo.set(null);
		this.envoyerVideo(qualite, event.dataTransfer?.files?.[0]);
	}

	annulerVideo(qualite: Qualite): void {
		this.abonnementsVideo[qualite]?.unsubscribe();
		delete this.abonnementsVideo[qualite];
		this.majEnvoi(qualite, undefined);
	}

	/** Taille lisible d'une vidéo envoyée (ex. « 1,2 Go »). */
	tailleVideo(qualite: Qualite): string | null {
		const octets = this.film()?.videos.find((v) => v.qualite === qualite)?.tailleOctets;
		if (!octets) {
			return null;
		}
		return octets >= 1e9 ? `${(octets / 1e9).toLocaleString('fr-FR', { maximumFractionDigits: 1 })} Go` : `${Math.max(1, Math.round(octets / 1e6))} Mo`;
	}

	private envoyerVideo(qualite: Qualite, fichier: File | undefined): void {
		const film = this.film();
		if (!fichier || !film) {
			return;
		}
		const estMp4 = fichier.type === 'video/mp4' || fichier.name.toLowerCase().endsWith('.mp4');
		if (!estMp4 || fichier.size > TAILLE_MAX_VIDEO) {
			this.erreursVideo.update((e) => ({ ...e, [qualite]: 'Choisissez un fichier vidéo MP4 de 4 Go maximum.' }));
			return;
		}
		this.erreursVideo.update((e) => ({ ...e, [qualite]: undefined }));
		this.majEnvoi(qualite, 0);
		this.abonnementsVideo[qualite] = this.adminFilmService.envoyerVideo(film.id, qualite, fichier).subscribe({
			next: (evenement) => {
				if (evenement.type === HttpEventType.UploadProgress && evenement.total) {
					this.majEnvoi(qualite, Math.round((100 * evenement.loaded) / evenement.total));
				} else if (evenement.type === HttpEventType.Response && evenement.body) {
					this.film.set(evenement.body);
					// Le lien du fichier envoyé remplit le champ de la qualité (et sera conservé à l'enregistrement).
					const video = evenement.body.videos.find((v) => v.qualite === qualite);
					const controle = this.formulaire.controls.videos.controls[qualite];
					controle.setValue(video?.cheminHls ?? '');
					controle.markAsPristine();
					this.tailles[qualite] = video?.tailleOctets ?? null;
					this.majEnvoi(qualite, undefined);
					delete this.abonnementsVideo[qualite];
					this.messageService.succes('Vidéo enregistrée.');
				}
			},
			error: (err) => {
				this.majEnvoi(qualite, undefined);
				delete this.abonnementsVideo[qualite];
				this.erreursVideo.update((e) => ({ ...e, [qualite]: messageErreur(err, 'L’envoi de la vidéo a échoué.') }));
			},
		});
	}

	/**
	 * Envoie le fichier .vtt / .srt d'une ligne de sous-titres (langue et libellé de la ligne).
	 */
	choisirSousTitre(index: number, event: Event): void {
		const champ = event.target as HTMLInputElement;
		const fichier = champ.files?.[0];
		champ.value = '';
		const film = this.film();
		const ligne = this.sousTitres.at(index);
		if (!fichier || !film || !ligne) {
			return;
		}
		const erreur = (texte: string | undefined) =>
			this.erreursSousTitre.update((e) => {
				const copie = { ...e };
				if (texte) {
					copie[index] = texte;
				} else {
					delete copie[index];
				}
				return copie;
			});
		if (ligne.controls.langue.invalid || ligne.controls.libelle.invalid) {
			ligne.controls.langue.markAsTouched();
			ligne.controls.libelle.markAsTouched();
			erreur('Renseignez d’abord la langue et le libellé de la ligne.');
			return;
		}
		const nom = fichier.name.toLowerCase();
		if (!(nom.endsWith('.vtt') || nom.endsWith('.srt')) || fichier.size > 1024 * 1024) {
			erreur('Choisissez un fichier .vtt ou .srt de 1 Mo maximum.');
			return;
		}
		erreur(undefined);
		this.envoiSousTitre.set(index);
		const langue = ligne.controls.langue.value.trim().toLowerCase();
		this.adminFilmService.envoyerSousTitre(film.id, langue, ligne.controls.libelle.value.trim(), fichier).subscribe({
			next: (resultat) => {
				this.envoiSousTitre.set(null);
				this.film.set(resultat);
				const piste = resultat.sousTitres.find((s) => s.langue === langue);
				ligne.controls.langue.setValue(langue);
				ligne.controls.fichierVttUrl.setValue(piste?.fichierVttUrl ?? '');
				ligne.markAsPristine();
				this.messageService.succes('Sous-titres enregistrés.');
			},
			error: (err) => {
				this.envoiSousTitre.set(null);
				erreur(messageErreur(err, 'L’envoi des sous-titres a échoué.'));
			},
		});
	}

	private majEnvoi(qualite: Qualite, pourcentage: number | undefined): void {
		this.envoisVideo.update((envois) => {
			const copie = { ...envois };
			if (pourcentage === undefined) {
				delete copie[qualite];
			} else {
				copie[qualite] = pourcentage;
			}
			return copie;
		});
	}

	invalide(controle: { invalid: boolean; touched: boolean }): boolean {
		return controle.invalid && controle.touched;
	}
}
