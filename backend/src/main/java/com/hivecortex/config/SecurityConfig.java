package com.hivecortex.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

/**
 * Base security configuration — wires CORS and sets stateless session policy.
 *
 * <p>All endpoints are currently open (permitAll) so the health check is
 * accessible while authentication is not yet implemented. Prompt 008 replaces
 * the {@code anyRequest().permitAll()} line with JWT filter + authenticated().</p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(CorsConfigurationSource corsConfigurationSource) {
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Wire CORS through Spring Security — do NOT also register a CorsFilter bean,
            // as that would cause double-processing of CORS headers.
            .cors(cors -> cors.configurationSource(corsConfigurationSource))

            // CSRF disabled: this API is stateless (JWT), not session-cookie-based.
            .csrf(AbstractHttpConfigurer::disable)

            // Stateless: no HttpSession created or used — correct posture for JWT.
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth
                // Health check is always public.
                .requestMatchers("/api/v1/health").permitAll()
                // TODO (Prompt 008): Replace with .authenticated() and add JWT filter.
                .anyRequest().permitAll()
            );

        return http.build();
    }
}
