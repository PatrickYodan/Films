package com.films.ngoma.controller;

import com.films.ngoma.facade.PaysFacade;
import com.films.ngoma.presentation.dto.PaysDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/pays")
public class PaysController {

	private final PaysFacade facade;

	public PaysController(PaysFacade facade) {
		this.facade = facade;
	}

	/**
	 * @return la liste {@link PaysDto}
	 */
	@GetMapping("/lister")
	public List<PaysDto> lister() {
		return facade.lister();
	}
}
