package com.films.ngoma.controller;

import com.films.ngoma.facade.GenreFacade;
import com.films.ngoma.presentation.dto.GenreDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/genre")
public class GenreController {

	private final GenreFacade facade;

	public GenreController(GenreFacade facade) {
		this.facade = facade;
	}

	/**
	 * @return la liste {@link GenreDto}
	 */
	@GetMapping("/lister")
	public List<GenreDto> lister() {
		return facade.lister();
	}
}
