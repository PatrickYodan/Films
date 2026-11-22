package com.films.ngoma;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NgomaApplication {

	private static final Logger log = LoggerFactory.getLogger(NgomaApplication.class);

	public static void main(String[] args) {
		Environment env = SpringApplication.run(NgomaApplication.class, args).getEnvironment();
		log.info("""

				----------------------------------------------------------
				  Application '{}' démarrée
				  API     : http://localhost:{}{}
				  Swagger : http://localhost:{}/swagger-ui.html


				  Lancement de ngoma
				----------------------------------------------------------""",
				env.getProperty("app.name"),
				env.getProperty("server.port"), env.getProperty("api.prefix"),
				env.getProperty("server.port"));
	}
}
