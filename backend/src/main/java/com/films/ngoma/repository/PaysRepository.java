package com.films.ngoma.repository;

import com.films.ngoma.domain.Pays;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaysRepository extends JpaRepository<Pays, String> {

	List<Pays> findAllByOrderByNomAsc();
}
