package com.films.ngoma.configuration.openapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "NGOMA API", version = "v1",
		description = "API de la plateforme de streaming de films africains NGOMA"))
@SecurityScheme(name = "Authorization", type = SecuritySchemeType.HTTP, scheme = "bearer",
		bearerFormat = "JWT", in = SecuritySchemeIn.HEADER)
public class OpenApi30Config {
}
