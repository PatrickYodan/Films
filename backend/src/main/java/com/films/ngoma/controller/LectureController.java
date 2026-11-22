package com.films.ngoma.controller;

import com.films.ngoma.configuration.logger.Logged;
import com.films.ngoma.enums.Qualite;
import com.films.ngoma.facade.LectureFacade;
import com.films.ngoma.presentation.dto.lecture.LectureDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/lecture")
@SecurityRequirement(name = "Authorization")
public class LectureController {

	private final LectureFacade lectureFacade;

	public LectureController(LectureFacade lectureFacade) {
		this.lectureFacade = lectureFacade;
	}

	/**
	 * Ouvre la lecture : 200 avec l'adresse du flux, 403 sans pass (film payant), 404 si la vidéo manque.
	 */
	@PostMapping("/{slug}")
	@Logged
	public LectureDto ouvrir(@PathVariable String slug, @RequestParam(required = false) Qualite qualite) {
		return lectureFacade.ouvrir(slug, qualite);
	}
}
