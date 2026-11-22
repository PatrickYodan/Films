package com.films.ngoma.controller;

import com.films.ngoma.facade.PassFacade;
import com.films.ngoma.presentation.dto.PassDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/pass")
public class PassController {

	private final PassFacade facade;

	public PassController(PassFacade facade) {
		this.facade = facade;
	}

	/**
	 * @return la liste {@link PassDto}
	 */
	@GetMapping("/lister")
	public List<PassDto> lister() {
		return facade.lister();
	}
}
