package com.hivecortex.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * CORS configuration — scoped to the known frontend origin read from
 * {@code CORS_ALLOWED_ORIGIN} environment variable (never wildcard).
 *
 * <p>The {@link CorsConfigurationSource} bean is injected into
 * {@link SecurityConfig} so Spring Security owns the CORS handling.
 * Using both Spring Security CORS and a separate CorsFilter would
 * result in double-processing — always wire through Security.</p>
 */
@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origin}")
    private String allowedOrigin;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Explicit origin — never use setAllowedOriginPatterns("*") here.
        config.setAllowedOrigins(List.of(allowedOrigin));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Authorization header is needed from Prompt 008 onward for JWT attachment.
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "X-Requested-With"));

        // Allow cookies/credentials (needed when auth cookies are used in Prompt 008+)
        config.setAllowCredentials(true);

        // Browser can cache the preflight response for 1 hour
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Apply to all /api/** paths — nothing else exposes cross-origin endpoints.
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
