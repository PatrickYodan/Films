/** 112 → « 1 h 52 » */
export function formaterDuree(minutes: number | null | undefined): string {
	if (!minutes) {
		return '';
	}
	const h = Math.floor(minutes / 60);
	const m = minutes % 60;
	return h ? `${h} h ${m.toString().padStart(2, '0')}` : `${m} min`;
}

/** 24 → « 24 h », 168 → « 7 jours » */
export function formaterDureePass(heures: number): string {
	if (heures < 48) {
		return `${heures} h`;
	}
	const jours = Math.round(heures / 24);
	return `${jours} jours`;
}

/** 1000 → « 1 000 FCFA » */
export function formaterFcfa(montant: number): string {
	return `${montant.toLocaleString('fr-FR')} FCFA`;
}
