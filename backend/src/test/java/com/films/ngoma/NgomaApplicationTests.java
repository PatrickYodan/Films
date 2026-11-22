package com.films.ngoma;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Démarrage complet de l'application (PostgreSQL + Flyway) : activé seulement si NGOMA_TEST_DB_URL est défini,
 * pour que « gradlew test » ne dépende pas d'une base et ne migre jamais la base de développement par surprise.
 */
@SpringBootTest(properties = "spring.datasource.url=${NGOMA_TEST_DB_URL}")
@ActiveProfiles("dev")
@EnabledIfEnvironmentVariable(named = "NGOMA_TEST_DB_URL", matches = ".+")
class NgomaApplicationTests {

	@Test
	void contextLoads() {
	}
}
