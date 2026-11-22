import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export class CustomValidators {
	/** Numéro de téléphone : indicatif facultatif, 8 à 15 chiffres. */
	static telephone(): ValidatorFn {
		return (control: AbstractControl): ValidationErrors | null => {
			const valeur = (control.value ?? '').toString().replace(/\s/g, '');
			if (!valeur) {
				return null;
			}
			return /^\+?[0-9]{8,15}$/.test(valeur) ? null : { telephone: true };
		};
	}

	/** Vérifie que deux champs du groupe sont identiques (ex. mot de passe / confirmation). */
	static correspondance(champ: string, confirmation: string): ValidatorFn {
		return (groupe: AbstractControl): ValidationErrors | null => {
			const a = groupe.get(champ)?.value;
			const b = groupe.get(confirmation)?.value;
			return a && b && a !== b ? { correspondance: true } : null;
		};
	}

	/** Le contrôle (un tableau) doit contenir au moins un élément (ex. genres, pays). */
	static auMoinsUn(): ValidatorFn {
		return (control: AbstractControl): ValidationErrors | null =>
			Array.isArray(control.value) && control.value.length > 0 ? null : { auMoinsUn: true };
	}
}
