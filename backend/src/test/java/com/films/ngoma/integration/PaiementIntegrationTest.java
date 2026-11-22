package com.films.ngoma.integration;

import com.films.ngoma.domain.Utilisateur;
import com.films.ngoma.repository.UtilisateurRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Parcours de paiement complet avec l'opérateur simulé, sur une vraie base PostgreSQL
 * (activé si NGOMA_TEST_DB_URL est défini ; tout est annulé en fin de test).
 */
@SpringBootTest(properties = {
		"spring.datasource.url=${NGOMA_TEST_DB_URL}",
		"paiement.simulation.delai_secondes=0"
})
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("dev")
@EnabledIfEnvironmentVariable(named = "NGOMA_TEST_DB_URL", matches = ".+")
@WithMockUser(username = PaiementIntegrationTest.EMAIL, roles = "ABONNE")
class PaiementIntegrationTest {

	static final String EMAIL = "test-paiement@ngoma.test";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UtilisateurRepository utilisateurRepository;

	@BeforeEach
	void creerAbonne() {
		utilisateurRepository.saveAndFlush(new Utilisateur("Test Paiement", EMAIL, null, "inutilise"));
	}

	private String initier(String telephone) throws Exception {
		String reponse = mockMvc.perform(post("/ws/paiement/initier").contentType(MediaType.APPLICATION_JSON)
						.content("{\"codePass\": \"PASS_JOUR\", \"moyen\": \"ORANGE_MONEY\", \"telephone\": \"" + telephone + "\"}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.statut").value("EN_ATTENTE"))
				.andExpect(jsonPath("$.modeTest").value(true))
				.andReturn().getResponse().getContentAsString();
		return JsonPath.read(reponse, "$.reference");
	}

	@Test
	void paiementConfirmeActiveLePassEtOuvreLaLecture() throws Exception {
		mockMvc.perform(get("/ws/abonnement/courant")).andExpect(status().isNoContent());
		// Avant paiement : film payant refusé
		mockMvc.perform(post("/ws/lecture/lagos-express"))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.code").value(7401));

		String reference = initier("+2250700000001");

		mockMvc.perform(get("/ws/paiement/" + reference))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.statut").value("CONFIRME"));
		mockMvc.perform(get("/ws/abonnement/courant"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.pass.code").value("PASS_JOUR"))
				.andExpect(jsonPath("$.statut").value("ACTIF"));

		// Après paiement : la vidéo est servie
		mockMvc.perform(post("/ws/lecture/lagos-express").param("qualite", "HD_1080P"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.urlHls").value(org.hamcrest.Matchers.endsWith(".m3u8")))
				.andExpect(jsonPath("$.qualite").value("HD_1080P"));
	}

	@Test
	void refusSimuleNActivePasLePass() throws Exception {
		String reference = initier("+2250700000000");

		mockMvc.perform(get("/ws/paiement/" + reference)).andExpect(jsonPath("$.statut").value("ECHOUE"));
		mockMvc.perform(get("/ws/abonnement/courant")).andExpect(status().isNoContent());
	}
}
