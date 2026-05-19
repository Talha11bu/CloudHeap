package com.talha11bu.silkroad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Entry point for the SilkRoad application.
 *
 * <p>SilkRoad is an ephemeral, real-time file-sharing platform that creates
 * temporary, password-protected sessions for secure data exchange. Sessions
 * auto-expire after a configurable duration, leaving no persistent traces.</p>
 *
 * <p>Enables scheduled task execution for automatic cleanup of expired sessions
 * and their associated R2 storage objects.</p>
 *
 * @see com.talha11bu.silkroad.services.SessionService#cleanupExpiredSessions()
 */
@EnableScheduling
@SpringBootApplication
public class SilkroadApplication {

	/**
	 * Bootstraps the Spring Boot application context and starts the embedded server.
	 *
	 * @param args command-line arguments passed at startup.
	 */
	public static void main(String[] args) {
		SpringApplication.run(SilkroadApplication.class, args);
	}

}
