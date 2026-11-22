package com.films.ngoma.service.paiement;

import com.films.ngoma.domain.Paiement;
import com.films.ngoma.service.paiement.OperateurPaiementClient.Etat;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SimulationPaiementClientTest {

	private static Paiement paiement(String telephone) {
		Paiement p = mock(Paiement.class);
		when(p.getReference()).thenReturn("NG20261113ABCD");
		when(p.getTelephone()).thenReturn(telephone);
		return p;
	}

	@Test
	void confirmeApresLeDelai() {
		SimulationPaiementClient client = new SimulationPaiementClient(0, "");
		var statut = client.consulter(paiement("+2250700000001"));
		assertEquals(Etat.CONFIRME, statut.etat());
		assertEquals("SIM-NG20261113ABCD", statut.referenceOperateur());
	}

	@Test
	void resteEnAttentePendantLeDelai() {
		SimulationPaiementClient client = new SimulationPaiementClient(3600, "");
		assertEquals(Etat.EN_ATTENTE, client.consulter(paiement("+2250700000001")).etat());
	}

	@Test
	void numeroTerminantPar00SimuleUnRefus() {
		SimulationPaiementClient client = new SimulationPaiementClient(0, "");
		assertEquals(Etat.ECHOUE, client.consulter(paiement("+2250700000000")).etat());
	}

	@Test
	void notificationProtegeeParLeSecret() {
		assertTrue(new SimulationPaiementClient(0, "secret").notificationAuthentique("secret", null));
		assertFalse(new SimulationPaiementClient(0, "secret").notificationAuthentique("autre", null));
		assertFalse(new SimulationPaiementClient(0, "").notificationAuthentique("", null));
	}

	@Test
	void signaleLeModeTest() {
		assertTrue(new SimulationPaiementClient(0, "").modeTest());
	}
}
