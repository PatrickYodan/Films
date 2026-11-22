package com.films.ngoma.facade;

import com.films.ngoma.enums.StatutFilm;
import com.films.ngoma.exception.FilmException;
import com.films.ngoma.presentation.dto.PageDto;
import com.films.ngoma.presentation.dto.film.FilmDetailDto;
import com.films.ngoma.presentation.dto.film.FilmResumeDto;
import com.films.ngoma.presentation.dto.film.RechercheFilmDto;
import com.films.ngoma.repository.FilmRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class FilmFacade {

	private final FilmRepository filmRepository;

	public FilmFacade(FilmRepository filmRepository) {
		this.filmRepository = filmRepository;
	}

	/**
	 * Recherche dans le catalogue selon les critères renseignés.
	 */
	@Transactional(readOnly = true)
	public PageDto<FilmResumeDto> rechercher(RechercheFilmDto criteres, Pageable pageable) {
		String motCle = criteres.motCle() == null || criteres.motCle().isBlank()
				? "%"
				: "%" + criteres.motCle().trim().toLowerCase(Locale.ROOT) + "%";
		String paysCode = criteres.paysCode() == null || criteres.paysCode().isBlank()
				? null
				: criteres.paysCode().trim().toUpperCase(Locale.ROOT);
		return new PageDto<>(filmRepository
				.rechercher(motCle, criteres.genreId(), paysCode, criteres.gratuit(), pageable)
				.map(FilmResumeDto::new));
	}

	/**
	 * @return la fiche complète du film.
	 */
	@Transactional(readOnly = true)
	public FilmDetailDto detail(String slug) {
		return filmRepository.findBySlugAndStatut(slug, StatutFilm.PUBLIE)
				.map(FilmDetailDto::new)
				.orElseThrow(() -> FilmException.filmInconnu(slug));
	}
}
