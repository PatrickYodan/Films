package com.films.ngoma.facade;

import com.films.ngoma.domain.Abonnement;
import com.films.ngoma.domain.Paiement;
import com.films.ngoma.domain.Pass;
import com.films.ngoma.domain.Utilisateur;
import com.films.ngoma.enums.MoyenPaiement;
import com.films.ngoma.enums.StatutPaiement;
import com.films.ngoma.exception.PaiementException;
import com.films.ngoma.presentation.dto.paiement.NotificationPaiementDto;
import com.films.ngoma.repository.AbonnementRepository;
import com.films.ngoma.repository.PaiementRepository;
import com.films.ngoma.repository.PassRepository;
import com.films.ngoma.service.SecurityService;
import com.films.ngoma.service.paiement.OperateurPaiementClient;
import com.films.ngoma.service.paiement.OperateurPaiementClient.Etat;
import com.films.ngoma.service.paiement.OperateurPaiementClient.StatutOperateur;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Activation du pass selon l'état renvoyé par l'opérateur (dépôts et opérateur simulés).
 */
class PaiementFacadeTest {

	private PaiementRepository paiementRepository;
	private AbonnementRepository abonnementRepository;
	private OperateurPaiementClient operateur;
	private PaiementFacade facade;
	private Utilisateur utilisateur;
	private Pass pass;

	@BeforeEach
	void preparer() {
		paiementRepository = mock(PaiementRepository.class);
		abonnementRepository = mock(AbonnementRepository.class);
		operateur = mock(OperateurPaiementClient.class);
		facade = new PaiementFacade(paiementRepository, mock(PassRepository.class), abonnementRepository,
				mock(SecurityService.class), operateur, 15);
		utilisateur = mock(Utilisateur.class);
		when(utilisateur.getId()).thenReturn(1L);
		pass = mock(Pass.class);
		when(pass.getDureeHeures()).thenReturn(24);
		when(pass.getPrixFcfa()).thenReturn(200);
		when(abonnementRepository.save(any(Abonnement.class))).thenAnswer(i -> i.getArgument(0));
	}

	private Paiement paiement() {
		return new Paiement("NG1", utilisateur, pass, MoyenPaiement.ORANGE_MONEY, "+2250700000001");
	}

	@Test
	void confirmationActiveLePass() {
		Paiement p = paiement();
		when(operateur.consulter(p)).thenReturn(new StatutOperateur(Etat.CONFIRME, "OP-1"));
		when(abonnementRepository.findFirstByUtilisateurIdAndStatutAndDateFinAfterOrderByDateFinDesc(anyLong(), any(), any()))
				.thenReturn(Optional.empty());

		assertTrue(facade.synchroniser(p));
		assertEquals(StatutPaiement.CONFIRME, p.getStatut());
		assertEquals("OP-1", p.getReferenceOperateur());
		assertTrue(p.getAbonnement().getDateFin().isAfter(LocalDateTime.now().plusHours(23)));
	}

	@Test
	void unNouveauPassProlongeLAbonnementEnCours() {
		Paiement p = paiement();
		LocalDateTime finActuelle = LocalDateTime.now().plusDays(3);
		Abonnement enCours = mock(Abonnement.class);
		when(enCours.getDateFin()).thenReturn(finActuelle);
		when(operateur.consulter(p)).thenReturn(new StatutOperateur(Etat.CONFIRME, "OP-1"));
		when(abonnementRepository.findFirstByUtilisateurIdAndStatutAndDateFinAfterOrderByDateFinDesc(eq(1L), any(), any()))
				.thenReturn(Optional.of(enCours));

		facade.synchroniser(p);
		assertEquals(finActuelle, p.getAbonnement().getDateDebut());
		assertEquals(finActuelle.plusHours(24), p.getAbonnement().getDateFin());
	}

	@Test
	void refusDeLOperateur() {
		Paiement p = paiement();
		when(operateur.consulter(p)).thenReturn(new StatutOperateur(Etat.ECHOUE, "OP-2"));
		assertTrue(facade.synchroniser(p));
		assertEquals(StatutPaiement.ECHOUE, p.getStatut());
		verify(abonnementRepository, never()).save(any());
	}

	@Test
	void enAttenteNeChangeRien() {
		Paiement p = paiement();
		when(operateur.consulter(p)).thenReturn(new StatutOperateur(Etat.EN_ATTENTE, null));
		assertFalse(facade.synchroniser(p));
		assertEquals(StatutPaiement.EN_ATTENTE, p.getStatut());
	}

	@Test
	void notificationNonAuthentiqueRefusee() {
		when(operateur.notificationAuthentique(any(), any())).thenReturn(false);
		assertThrows(PaiementException.class,
				() -> facade.traiterNotification("faux", new NotificationPaiementDto("NG1", "OP-1", true)));
		verify(paiementRepository, never()).findByReference(any());
	}

	@Test
	void notificationRejoueeSurUnPaiementDejaConfirmeIgnoree() {
		Paiement p = paiement();
		p.confirmer("OP-1", mock(Abonnement.class));
		when(operateur.notificationAuthentique(any(), any())).thenReturn(true);
		when(paiementRepository.findByReference("NG1")).thenReturn(Optional.of(p));

		facade.traiterNotification("ok", new NotificationPaiementDto("NG1", "OP-1", true));
		verify(operateur, never()).consulter(any());
	}

	@Test
	void verificationDeSecoursTraiteLesPaiementsEnAttente() {
		Paiement p = paiement();
		when(paiementRepository.findByStatutAndCreateAtAfter(eq(StatutPaiement.EN_ATTENTE), any())).thenReturn(List.of(p));
		when(operateur.consulter(p)).thenReturn(new StatutOperateur(Etat.ECHOUE, "OP-3"));
		assertEquals(1, facade.verifierPaiementsEnAttente());
	}
}
