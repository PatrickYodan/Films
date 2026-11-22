package com.films.ngoma.facade;

import com.films.ngoma.presentation.dto.GenreDto;
import com.films.ngoma.repository.GenreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GenreFacade {

	private final GenreRepository genreRepository;

	public GenreFacade(GenreRepository genreRepository) {
		this.genreRepository = genreRepository;
	}

	@Transactional(readOnly = true)
	public List<GenreDto> lister() {
		return genreRepository.findAllByOrderByNomAsc().stream().map(GenreDto::new).toList();
	}
}
