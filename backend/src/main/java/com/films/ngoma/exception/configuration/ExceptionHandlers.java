package com.films.ngoma.exception.configuration;

import com.films.ngoma.exception.LectureException;
import com.films.ngoma.exception.UtilisateurException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static com.films.ngoma.exception.configuration.CodeErreurTechnique.ACCES_REFUSE;
import static com.films.ngoma.exception.configuration.CodeErreurTechnique.ERREUR_INCONNUE;
import static com.films.ngoma.exception.configuration.CodeErreurTechnique.ERREUR_VERSION;

/**
 * Gestion centralisée des exceptions Spring MVC : toutes les erreurs sont renvoyées
 * au format {@link ApplicationErreur}.
 */
@RestControllerAdvice
public class ExceptionHandlers extends ResponseEntityExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(ExceptionHandlers.class);

	@ExceptionHandler(EntiteNonConnueException.class)
	public ResponseEntity<ApplicationErreur> handleEntiteNonConnue(EntiteNonConnueException ex) {
		log.warn(ex.getMessageAvecCode());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getApplicationErreur());
	}

	@ExceptionHandler(LectureException.class)
	public ResponseEntity<ApplicationErreur> handleLecture(LectureException ex) {
		log.info(ex.getMessageAvecCode());
		return ResponseEntity.status(ex.getStatut()).body(ex.getApplicationErreur());
	}

	@ExceptionHandler(UtilisateurException.class)
	public ResponseEntity<ApplicationErreur> handleUtilisateur(UtilisateurException ex) {
		log.warn(ex.getMessageAvecCode());
		return ResponseEntity.status(ex.getStatut()).body(ex.getApplicationErreur());
	}

	@ExceptionHandler(AbstractApplicationException.class)
	public ResponseEntity<ApplicationErreur> handleApplication(AbstractApplicationException ex) {
		log.warn(ex.getMessageAvecCode());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getApplicationErreur());
	}

	@ExceptionHandler({AccessDeniedException.class, AuthenticationException.class})
	public ResponseEntity<ApplicationErreur> handleAccesRefuse(RuntimeException ex) {
		ApplicationErreur erreur = new ApplicationErreur(ACCES_REFUSE, "Accès refusé");
		log.warn(erreur.getMessageAvecCode());
		HttpStatus statut = ex instanceof AuthenticationException ? HttpStatus.UNAUTHORIZED : HttpStatus.FORBIDDEN;
		return ResponseEntity.status(statut).body(erreur);
	}

	@ExceptionHandler(ObjectOptimisticLockingFailureException.class)
	public ResponseEntity<ApplicationErreur> handleOptimisticLock(ObjectOptimisticLockingFailureException ex) {
		ApplicationErreur erreur = new ApplicationErreur(ERREUR_VERSION,
				"Cette donnée a déjà été modifiée. Veuillez recharger la page et réessayer.");
		log.warn(erreur.getMessageAvecCode());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(erreur);
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ApplicationErreur> handleInconnue(RuntimeException ex) {
		ApplicationErreur erreur = new ApplicationErreur(ERREUR_INCONNUE,
				"Une erreur inattendue s'est produite. Veuillez réessayer plus tard.");
		log.error(erreur.getMessageAvecCode(), ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erreur);
	}
}
