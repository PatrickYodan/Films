package com.films.ngoma.facade;

import com.films.ngoma.presentation.dto.PaysDto;
import com.films.ngoma.repository.PaysRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaysFacade {

	private final PaysRepository paysRepository;

	public PaysFacade(PaysRepository paysRepository) {
		this.paysRepository = paysRepository;
	}

	@Transactional(readOnly = true)
	public List<PaysDto> lister() {
		return paysRepository.findAllByOrderByNomAsc().stream().map(PaysDto::new).toList();
	}
}
