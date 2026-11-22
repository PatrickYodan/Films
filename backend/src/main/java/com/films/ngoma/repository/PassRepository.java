package com.films.ngoma.repository;

import com.films.ngoma.domain.Pass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PassRepository extends JpaRepository<Pass, Long> {

	List<Pass> findByActifTrueOrderByDureeHeuresAsc();

	Optional<Pass> findByCodeAndActifTrue(String code);
}
