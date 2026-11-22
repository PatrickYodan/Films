export interface Auth {
	email: string;
	motDePasse: string;
}

export interface Inscription {
	nom: string;
	email: string;
	telephone: string | null;
	motDePasse: string;
}
