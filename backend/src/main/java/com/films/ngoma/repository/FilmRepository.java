package com.films.ngoma.repository;

import com.films.ngoma.domain.Film;
import com.films.ngoma.enums.StatutFilm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FilmRepository extends JpaRepository<Film, Long> {

	boolean existsBySlug(String slug);

	/**
	 * Slugs déjà pris égaux à {@code base} ou de la forme {@code base-…} (pour générer {@code base-2}, {@code base-3}…).
	 * Un slug ne contient que [a-z0-9-] : pas de caractère joker LIKE à échapper.
	 */
	@Query("select f.slug from Film f where f.slug = :base or f.slug like concat(:base, '-%')")
	List<String> slugsCommencantPar(@Param("base") String base);

	/** Fiche publique : uniquement les films publiés. */
	@EntityGraph(attributePaths = {"genres", "pays"})
	Optional<Film> findBySlugAndStatut(String slug, StatutFilm statut);

	/**
	 * Recherche du back-office (tous statuts), du plus récemment modifié au plus ancien.
	 * {@code motCle} est un motif LIKE en minuscules, appliqué au titre et au slug.
	 */
	@Query(value = """
			select f from Film f
			where (lower(f.titre) like :motCle or f.slug like :motCle)
			  and (:statut is null or f.statut = :statut)
			order by coalesce(f.updateAt, f.createAt) desc, f.id desc
			""",
			countQuery = """
			select count(f) from Film f
			where (lower(f.titre) like :motCle or f.slug like :motCle)
			  and (:statut is null or f.statut = :statut)
			""")
	Page<Film> rechercherAdmin(@Param("motCle") String motCle, @Param("statut") StatutFilm statut, Pageable pageable);

	/**
	 * Recherche multicritère du catalogue public (films publiés uniquement).
	 * {@code motCle} doit être un motif LIKE en minuscules (ex. "%").
	 */
	@Query("""
			select f from Film f
			where f.statut = com.films.ngoma.enums.StatutFilm.PUBLIE
			  and lower(f.titre) like :motCle
			  and (:gratuit is null or f.gratuit = :gratuit)
			  and (:genreId is null or exists (
			        select 1 from Film f2 join f2.genres g where f2 = f and g.id = :genreId))
			  and (:paysCode is null or exists (
			        select 1 from Film f3 join f3.pays p where f3 = f and p.code = :paysCode))
			""")
	Page<Film> rechercher(@Param("motCle") String motCle,
						  @Param("genreId") Long genreId,
						  @Param("paysCode") String paysCode,
						  @Param("gratuit") Boolean gratuit,
						  Pageable pageable);
}
