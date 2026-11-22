import { Qualite } from './qualite.model';

export interface PisteSousTitre {
	langue: string;
	libelle: string;
	url: string;
}

/** Autorisation de lecture renvoyée par POST /ws/lecture/{slug} (voir LectureDto). */
export interface Lecture {
	urlHls: string;
	/** Qualité effectivement servie (plafonnée par le pass). */
	qualite: Qualite;
	qualitesDisponibles: Qualite[];
	sousTitres: PisteSousTitre[];
	/** HLS : manifeste .m3u8 lu avec hls.js ; MP4 : fichier lu directement par le navigateur. */
	format: 'HLS' | 'MP4';
}
