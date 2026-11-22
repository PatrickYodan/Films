/** Page de résultats (voir PageDto côté backend). */
export interface Page<T> {
	contenu: T[];
	page: number;
	taille: number;
	total: number;
	totalPages: number;
}
