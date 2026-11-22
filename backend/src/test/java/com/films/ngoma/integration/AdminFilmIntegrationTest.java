package com.films.ngoma.integration;

import com.films.ngoma.domain.Film;
import com.films.ngoma.repository.FilmRepository;
import com.films.ngoma.repository.GenreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Parcours complet sur une vraie base PostgreSQL : activé seulement si NGOMA_TEST_DB_URL est défini
 * (ex. jdbc:postgresql://localhost:5432/ngoma_test). Identifiants : ceux de application.properties,
 * ou les variables standard SPRING_DATASOURCE_USERNAME / SPRING_DATASOURCE_PASSWORD. Chaque test est annulé en fin d'exécution (@Transactional).
 */
@SpringBootTest(properties = "spring.datasource.url=${NGOMA_TEST_DB_URL}")
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("dev")
@EnabledIfEnvironmentVariable(named = "NGOMA_TEST_DB_URL", matches = ".+")
@WithMockUser(username = "test-admin", roles = "ADMIN")
class AdminFilmIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private FilmRepository filmRepository;

	@Autowired
	private GenreRepository genreRepository;

	private String edition(String titre, String synopsis, Long version) {
		long genre = genreRepository.findAll().get(0).getId();
		return """
				{"titre": "%s", "type": "FILM", "synopsis": %s, "genreIds": [%d], "paysCodes": ["CI"],
				 "gratuit": false, "exclusivite": false, "telechargeable": false, "version": %s}
				""".formatted(titre, synopsis == null ? "null" : "\"" + synopsis + "\"", genre, version);
	}

	@Test
	void creationBrouillonSlugUniqueEtInvisibleDuPublic() throws Exception {
		mockMvc.perform(post("/ws/admin/film").contentType(MediaType.APPLICATION_JSON).content(edition("Zz Essai Intégration", null, null)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.statut").value("BROUILLON"))
				.andExpect(jsonPath("$.slug").value("zz-essai-integration"));
		mockMvc.perform(post("/ws/admin/film").contentType(MediaType.APPLICATION_JSON).content(edition("Zz Essai Intégration", null, null)))
				.andExpect(jsonPath("$.slug").value("zz-essai-integration-2"));

		mockMvc.perform(get("/ws/film/lister").param("motCle", "zz essai"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.total").value(0));
		mockMvc.perform(get("/ws/film/zz-essai-integration")).andExpect(status().isNotFound());
	}

	@Test
	void publicationRefuseePuisAccepteeUneFoisLeFilmComplet() throws Exception {
		mockMvc.perform(post("/ws/admin/film").contentType(MediaType.APPLICATION_JSON).content(edition("Zz Essai Publication", "Un synopsis.", null)))
				.andExpect(status().isCreated());
		Film film = filmRepository.findBySlugAndStatut("zz-essai-publication", com.films.ngoma.enums.StatutFilm.BROUILLON).orElseThrow();

		mockMvc.perform(post("/ws/admin/film/" + film.getId() + "/publier"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value(7102));

		// Images posées directement (l'envoi de fichier est couvert par les tests sans base)
		film.changerAffiche("https://cdn.example.com/zz/affiche.jpg");
		film.changerBandeau("https://cdn.example.com/zz/bandeau.jpg");
		filmRepository.saveAndFlush(film);
		mockMvc.perform(put("/ws/admin/film/" + film.getId() + "/videos").contentType(MediaType.APPLICATION_JSON)
						.content("[{\"qualite\": \"STANDARD_480P\", \"cheminHls\": \"films/zz/480p/index.m3u8\"}]"))
				.andExpect(status().isOk());

		mockMvc.perform(post("/ws/admin/film/" + film.getId() + "/publier"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.statut").value("PUBLIE"));
		mockMvc.perform(get("/ws/film/lister").param("motCle", "zz essai"))
				.andExpect(jsonPath("$.contenu[*].slug", hasItem("zz-essai-publication")));
		mockMvc.perform(get("/ws/film/zz-essai-publication")).andExpect(status().isOk());

		mockMvc.perform(post("/ws/admin/film/" + film.getId() + "/depublier")).andExpect(status().isOk());
		mockMvc.perform(get("/ws/film/lister").param("motCle", "zz essai"))
				.andExpect(jsonPath("$.contenu[*].slug", not(hasItem("zz-essai-publication"))));
	}
}
