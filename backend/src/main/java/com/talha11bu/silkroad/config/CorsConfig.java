package com.talha11bu.silkroad.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Global CORS configuration for the SilkRoad REST API.
 *
 * <p>Allows the frontend development server to make cross-origin requests
 * to all API endpoints. Credentials (cookies, Authorization headers) are
 * permitted, and pre-flight responses are cached for 2400 seconds.</p>
 */
@Configuration
public class CorsConfig {

	/**
	 * Registers a global CORS mapping that applies to all endpoints.
	 *
	 * @return a {@link WebMvcConfigurer} with the CORS rules applied.
	 */
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // API endpoint
                        .allowedOrigins("http://localhost:5173") // frontend URL 
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .maxAge(2400);
            }
		};
	}
}
