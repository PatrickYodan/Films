package com.films.ngoma.controller;

import com.films.ngoma.exception.FilmException;
import com.films.ngoma.facade.AdminFilmFacade;
import com.films.ngoma.presentation.dto.PageDto;
import com.films.ngoma.security.JwtTokenUtils;
import com.films.ngoma.security.LogoutService;
import com.films.ngoma.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Accès au back-office : vraie configuration de sécurité, façade simulée (sans base de données).
 */
@WebMvcTest(AdminFilmController.class)
@Import({SecurityConfig.class, LogoutService.class})
class AdminFilmControllerSecuriteTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AdminFilmFacade adminFilmFacade;

	/** Requis par JwtRequestFilter (les tests s'authentifient avec @WithMockUser). */
	@MockitoBean
	private UserDetailsService userDetailsService;

	@MockitoBean
	private JwtTokenUtils jwtTokenUtils;

	@Test
	void anonymeRecoit401() throws Exception {
		mockMvc.perform(get("/ws/admin/film/lister")).andExpect(status().isUnauthorized());
		verifyNoInteractions(adminFilmFacade);
	}

	@Test
	@WithMockUser(roles = "ABONNE")
	void abonneRecoit403() throws Exception {
		mockMvc.perform(get("/ws/admin/film/lister")).andExpect(status().isForbidden());
		mockMvc.perform(post("/ws/admin/film/1/publier")).andExpect(status().isForbidden());
		verifyNoInteractions(adminFilmFacade);
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void adminAccede() throws Exception {
		when(adminFilmFacade.lister(any(), any(), any())).thenReturn(new PageDto<>(List.of(), 0, 20, 0, 0));
		mockMvc.perform(get("/ws/admin/film/lister")).andExpect(status().isOk()).andExpect(jsonPath("$.total").value(0));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void creationRefuseeSansGenreNiPays() throws Exception {
		String corps = """
				{"titre": "Sans genre", "type": "FILM", "genreIds": [], "paysCodes": [],
				 "gratuit": false, "exclusivite": false, "telechargeable": false}
				""";
		mockMvc.perform(post("/ws/admin/film").contentType(MediaType.APPLICATION_JSON).content(corps))
				.andExpect(status().isBadRequest());
		verifyNoInteractions(adminFilmFacade);
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void uploadRefuseSiCeNEstPasUneImage() throws Exception {
		when(adminFilmFacade.changerAffiche(anyLong(), any())).thenThrow(FilmException.imageInvalide());
		MockMultipartFile faux = new MockMultipartFile("fichier", "affiche.png", "image/png", "pas une image".getBytes());
		mockMvc.perform(multipart("/ws/admin/film/1/affiche").file(faux))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value(7103));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	void publicationRefuseeSiIncomplet() throws Exception {
		when(adminFilmFacade.publier(eq(1L))).thenThrow(FilmException.filmIncomplet(List.of("l'affiche")));
		mockMvc.perform(post("/ws/admin/film/1/publier"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code").value(7102));
	}
}
