export interface Pays {
	code: string;
	nom: string;
	drapeauUrl: string | null;
}

/** Drapeau emoji à partir du code ISO 3166-1 alpha-2. */
export function drapeau(code: string): string {
	return String.fromCodePoint(...[...code.toUpperCase()].map((c) => 0x1f1e6 + c.charCodeAt(0) - 65));
}
