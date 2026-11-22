package com.films.ngoma.facade;

import com.films.ngoma.presentation.dto.PassDto;
import com.films.ngoma.repository.PassRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PassFacade {

	private final PassRepository passRepository;

	public PassFacade(PassRepository passRepository) {
		this.passRepository = passRepository;
	}

	/**
	 * @return les pass proposés à la vente, du plus court au plus long.
	 */
	@Transactional(readOnly = true)
	public List<PassDto> lister() {
		return passRepository.findByActifTrueOrderByDureeHeuresAsc().stream().map(PassDto::new).toList();
	}
}
