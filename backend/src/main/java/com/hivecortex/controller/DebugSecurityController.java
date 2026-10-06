package com.hivecortex.controller;

import com.hivecortex.config.JwtService;
import com.hivecortex.entity.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * Temporary debug controller for verifying JWT token generation and authentication
 * in isolation during Prompt 008 testing.
 */
@RestController
@RequestMapping("/api/v1/debug")
public class DebugSecurityController {

    private final JwtService jwtService;

    public DebugSecurityController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * Public debug endpoint to generate a test JWT.
     */
    @GetMapping("/token")
    public ResponseEntity<Map<String, Object>> generateTestToken(
            @RequestParam(defaultValue = "dev@hivecortex.com") String email,
            @RequestParam(defaultValue = "PROJECT_MANAGER") Role role
    ) {
        UUID dummyUserId = UUID.randomUUID();
        String token = jwtService.generateToken(email, dummyUserId, role);
        return ResponseEntity.ok(Map.of(
                "token", token,
                "email", email,
                "userId", dummyUserId,
                "role", role
        ));
    }

    /**
     * Protected debug endpoint requiring authentication.
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }
        return ResponseEntity.ok(Map.of(
                "principal", auth.getPrincipal(),
                "authorities", auth.getAuthorities().stream().map(Object::toString).toList()
        ));
    }
}
