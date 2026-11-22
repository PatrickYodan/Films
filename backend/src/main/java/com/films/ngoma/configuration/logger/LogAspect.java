package com.films.ngoma.configuration.logger;

import com.films.ngoma.security.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAspect {

	private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

	@Before("@annotation(com.films.ngoma.configuration.logger.Logged)")
	public void tracer(JoinPoint joinPoint) {
		logger.info("{} - {}", SecurityUtils.lireLoginUtilisateurConnecte(), joinPoint.getSignature().getName());
	}
}
