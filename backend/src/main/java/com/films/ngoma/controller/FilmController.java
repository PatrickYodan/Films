package com.films.ngoma.controller;

import com.films.ngoma.facade.FilmFacade;
import com.films.ngoma.presentation.dto.PageDto;
import com.films.ngoma.presentation.dto.film.FilmDetailDto;
import com.films.ngoma.presentation.dto.film.FilmResumeDto;
import com.films.ngoma.presentation.dto.film.RechercheFilmDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/film")
public class FilmController {

	private static final int TAILLE_MAX = 60;

	private final FilmFacade filmFacade;

	public FilmController(FilmFacade filmFacade) {
		this.filmFacade = filmFacade;
	}

	/**
	 * Recherche dans le catalogue (du plus récent au plus ancien).
	 */
	@GetMapping("/lister")
	public PageDto<FilmResumeDto> lister(@RequestParam(required = false) String motCle,
										 @RequestParam(required = false) Long genreId,
										 @RequestParam(required = false) String paysCode,
										 @RequestParam(required = false) Boolean gratuit,
										 @RequestParam(defaultValue = "0") int page,
										 @RequestParam(defaultValue = "20") int taille) {
		PageRequest pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(taille, 1), TAILLE_MAX),
				Sort.by(Sort.Order.desc("dateSortie"), Sort.Order.asc("titre")));
		return filmFacade.rechercher(new RechercheFilmDto(motCle, genreId, paysCode, gratuit), pageable);
	}

	/**
	 * @return la fiche du film
	 */
	@GetMapping("/{slug}")
	public FilmDetailDto detail(@PathVariable String slug) {
		return filmFacade.detail(slug);
	}
}
