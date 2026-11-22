import { HttpErrorResponse } from '@angular/common/http';
import { Component, DestroyRef, ElementRef, computed, effect, inject, input, linkedSignal, signal, untracked, viewChild } from '@angular/core';
import { takeUntilDestroyed, toObservable, toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import type Hls from 'hls.js';
import { catchError, of, switchMap, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { messageErreur } from '../../models/application-erreur.model';
import { Lecture } from '../../models/lecture.model';
import { QUALITES, Qualite } from '../../models/qualite.model';
import { FilmService } from '../../services/film.service';
import { formaterDuree } from '../../services/format';
import { LectureService } from '../../services/lecture.service';
import { CLE_QUALITE } from '../fiche-film/fiche-film.component';
import { ChargementComponent } from '../shared/chargement/chargement.component';
import { IconeComponent } from '../shared/icone/icone.component';

type EtatLecture = 'chargement' | 'pret' | 'pass' | 'indisponible' | 'erreur';

/** Langue de sous-titres choisie (« aucun » = désactivés), mémorisée d'un film à l'autre. */
const CLE_SOUS_TITRES = 'ngoma_sous_titres';
const AUCUN = 'aucun';

/** Hauteur d'image visée pour chaque qualité (choix du niveau dans un flux à plusieurs résolutions). */
const HAUTEURS: Record<Qualite, number> = { ECO_240P: 240, STANDARD_480P: 480, HD_1080P: 1080 };

/**
 * Lecteur vidéo HLS. Les droits (film gratuit ou pass actif) sont vérifiés par le backend
 * à l'ouverture (POST /ws/lecture/{slug}) ; hls.js n'est chargé qu'au moment de lire.
 */
@Component({
	selector: 'app-lecteur',
	imports: [RouterLink, ChargementComponent, IconeComponent],
	templateUrl: './lecteur.component.html',
	styleUrl: './lecteur.component.scss',
})
export class LecteurComponent {
	readonly slug = input.required<string>();
	/** Paramètre « qualite » de l'adresse (choisi sur la fiche film). */
	readonly qualite = input<Qualite | undefined>();

	private readonly filmService = inject(FilmService);
	private readonly lectureService = inject(LectureService);
	private readonly destroyRef = inject(DestroyRef);
	private readonly video = viewChild<ElementRef<HTMLVideoElement>>('video');
	private hls: Hls | null = null;
	/** Fin de la période où le choix de sous-titres de l'utilisateur prime sur la sélection automatique du navigateur. */
	private imposerChoixJusqua = 0;
	/** Position à reprendre après un changement de qualité. */
	private reprise = 0;

	protected readonly introuvable = signal(false);
	protected readonly film = toSignal(
		toObservable(this.slug).pipe(
			tap(() => this.introuvable.set(false)),
			switchMap((slug) =>
				this.filmService.detail(slug).pipe(
					catchError(() => {
						this.introuvable.set(true);
						return of(null);
					})
				)
			)
		),
		{ initialValue: null }
	);

	protected readonly etat = signal<EtatLecture>('chargement');
	protected readonly lecture = signal<Lecture | null>(null);
	protected readonly erreur = signal<string | null>(null);
	protected readonly qualiteChoisie = linkedSignal<Qualite>(
		() => this.qualite() ?? (localStorage.getItem(CLE_QUALITE) as Qualite | null) ?? 'STANDARD_480P'
	);
	protected readonly qualites = computed(() =>
		QUALITES.filter((q) => this.lecture()?.qualitesDisponibles.includes(q.code))
	);
	protected readonly meta = computed(() => {
		const f = this.film();
		return f ? [f.annee, formaterDuree(f.dureeMinutes), f.genres.map((g) => g.nom).join(', ')].filter(Boolean).join(' · ') : '';
	});
	protected readonly pistes = computed(() => this.lecture()?.sousTitres ?? []);
	protected readonly sousTitreChoisi = signal<string>(this.lireChoixSousTitres());
	/** Choix affiché : la langue choisie si ce film la propose, sinon « Désactivés ». */
	protected readonly sousTitreAffiche = computed(() =>
		this.pistes().some((p) => p.langue === this.sousTitreChoisi()) ? this.sousTitreChoisi() : AUCUN
	);
	protected readonly aucun = AUCUN;
	/** En développement, les vidéos sont des films libres de démonstration (mention de licence). */
	protected readonly demo = !environment.production;

	constructor() {
		effect(() => {
			const slug = this.slug();
			const qualite = this.qualiteChoisie();
			untracked(() => this.ouvrir(slug, qualite));
		});
		effect(() => {
			const element = this.video()?.nativeElement;
			const lecture = this.lecture();
			if (element && lecture) {
				untracked(() => this.brancher(element, lecture));
			}
		});
		this.destroyRef.onDestroy(() => this.detruire());
	}

	changerQualite(qualite: Qualite): void {
		localStorage.setItem(CLE_QUALITE, qualite);
		this.qualiteChoisie.set(qualite);
	}

	choisirSousTitre(langue: string): void {
		this.sousTitreChoisi.set(langue);
		this.memoriserChoixSousTitres(langue);
		this.appliquerSousTitres();
	}

	/** Affiche la piste de la langue choisie et masque les autres. */
	appliquerSousTitres(): void {
		const video = this.video()?.nativeElement;
		if (!video) {
			return;
		}
		const choix = this.sousTitreChoisi();
		for (const piste of Array.from(video.textTracks)) {
			piste.mode = piste.language === choix ? 'showing' : 'disabled';
		}
	}

	/**
	 * Au chargement, le navigateur peut activer de lui-même une piste (celle de la langue de son interface) :
	 * pendant ce court délai, le choix de l'utilisateur est réimposé au lieu d'être remplacé.
	 */
	appliquerAuChargement(): void {
		this.imposerChoixJusqua = Date.now() + 2000;
		this.appliquerSousTitres();
	}

	/** Garde le sélecteur à jour si l'utilisateur passe par le bouton « CC » natif du lecteur. */
	private synchroniserSousTitres(video: HTMLVideoElement): void {
		if (Date.now() < this.imposerChoixJusqua) {
			this.appliquerSousTitres();
			return;
		}
		const actives = Array.from(video.textTracks)
			.filter((piste) => piste.mode === 'showing')
			.map((piste) => piste.language);
		if (actives.includes(this.sousTitreChoisi())) {
			if (actives.length > 1) {
				this.appliquerSousTitres();
			}
			return;
		}
		const langue = actives[0] || AUCUN;
		this.sousTitreChoisi.set(langue);
		this.memoriserChoixSousTitres(langue);
	}

	private lireChoixSousTitres(): string {
		try {
			return localStorage.getItem(CLE_SOUS_TITRES) ?? AUCUN;
		} catch {
			return AUCUN;
		}
	}

	private memoriserChoixSousTitres(langue: string): void {
		try {
			localStorage.setItem(CLE_SOUS_TITRES, langue);
		} catch {
			// Stockage indisponible (navigation privée) : le choix vaut pour cette page seulement.
		}
	}

	reessayer(): void {
		this.etat.set('chargement');
		this.ouvrir(this.slug(), this.qualiteChoisie());
	}

	private ouvrir(slug: string, qualite: Qualite): void {
		this.reprise = this.video()?.nativeElement.currentTime ?? 0;
		if (this.etat() !== 'pret') {
			this.etat.set('chargement');
		}
		this.lectureService
			.ouvrir(slug, qualite)
			.pipe(takeUntilDestroyed(this.destroyRef))
			.subscribe({
				next: (lecture) => {
					this.lecture.set(lecture);
					this.etat.set('pret');
				},
				error: (err: unknown) => {
					this.detruire();
					this.lecture.set(null);
					const code = err instanceof HttpErrorResponse ? err.error?.code : null;
					if (err instanceof HttpErrorResponse && err.status === 403) {
						this.etat.set('pass');
					} else if (code === 7402) {
						this.etat.set('indisponible');
					} else {
						this.erreur.set(messageErreur(err, 'La lecture n’a pas pu démarrer.'));
						this.etat.set('erreur');
					}
				},
			});
	}

	/**
	 * Attache le flux HLS à l'élément vidéo. hls.js est utilisé dès qu'il est pris en charge : lui seul permet
	 * d'imposer la qualité choisie. La lecture native (que Chrome et Edge proposent aussi désormais) choisit
	 * la résolution elle-même : elle ne sert que de solution de repli (iPhone).
	 */
	private async brancher(video: HTMLVideoElement, lecture: Lecture): Promise<void> {
		const reprise = this.reprise;
		this.detruire();
		video.textTracks.onchange = () => this.synchroniserSousTitres(video);
		this.appliquerAuChargement();
		if (lecture.format === 'MP4') {
			// Fichier MP4 : lu directement par le navigateur (une seule résolution par fichier).
			this.lireDirectement(video, lecture.urlHls, reprise);
			return;
		}
		const { default: HlsJs } = await import('hls.js');
		if (!HlsJs.isSupported()) {
			if (video.canPlayType('application/vnd.apple.mpegurl')) {
				this.lireDirectement(video, lecture.urlHls, reprise);
			} else {
				this.erreur.set('Votre navigateur ne peut pas lire cette vidéo.');
				this.etat.set('erreur');
			}
			return;
		}
		// Chargement manuel : la résolution est fixée AVANT le premier segment (sinon hls.js démarre
		// à une résolution par défaut et la qualité choisie ne s'applique qu'après plusieurs secondes).
		const hls = new HlsJs({ autoStartLoad: false });
		this.hls = hls;
		hls.on(HlsJs.Events.MANIFEST_PARSED, (_evenement, donnees) => {
			const niveau = this.niveauPour(donnees.levels.map((n) => n.height), HAUTEURS[lecture.qualite]);			hls.startLevel = niveau;
			// Niveau imposé : la qualité choisie (et payée) est respectée, sans adaptation automatique.
			hls.loadLevel = niveau;
			hls.startLoad(reprise > 0 ? reprise : -1);
			// La lecture automatique peut être bloquée par le navigateur : l'utilisateur clique alors sur Lecture.
			video.play().catch(() => undefined);
		});
		hls.on(HlsJs.Events.ERROR, (_evenement, donnees) => {
			if (donnees.fatal) {
				this.detruire();
				this.erreur.set('La lecture a été interrompue. Vérifiez votre connexion puis réessayez.');
				this.etat.set('erreur');
			}
		});
		hls.loadSource(lecture.urlHls);
		hls.attachMedia(video);
	}

	/** Lecture native (fichier MP4, ou HLS sur iPhone), en reprenant à la position donnée. */
	private lireDirectement(video: HTMLVideoElement, url: string, reprise: number): void {
		video.src = url;
		video.addEventListener('loadedmetadata', () => (video.currentTime = reprise), { once: true });
		video.addEventListener(
			'error',
			() => {
				this.erreur.set('La vidéo n’a pas pu être lue. Vérifiez votre connexion puis réessayez.');
				this.etat.set('erreur');
			},
			{ once: true }
		);
		video.play().catch(() => undefined);
	}

	/** Index du niveau le plus haut ne dépassant pas la hauteur visée (sinon le plus bas). */
	private niveauPour(hauteurs: number[], cible: number): number {
		let choisi = -1;
		hauteurs.forEach((h, i) => {
			if (h <= cible && (choisi < 0 || h > hauteurs[choisi])) {
				choisi = i;
			}
		});
		return choisi >= 0 ? choisi : hauteurs.indexOf(Math.min(...hauteurs));
	}

	private detruire(): void {
		this.hls?.destroy();
		this.hls = null;
		const video = this.video()?.nativeElement;
		if (video?.src && !video.src.startsWith('blob:')) {
			// Arrête le téléchargement d'un MP4 en cours avant d'en charger un autre
			video.removeAttribute('src');
			video.load();
		}
	}
}
