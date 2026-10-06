package com.hivecortex.config;

import com.hivecortex.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", 86400000L);
    }

    @Test
    void testGenerateAndValidateToken() {
        String email = "developer@hivecortex.com";
        UUID userId = UUID.randomUUID();
        Role role = Role.DEVELOPER;

        String token = jwtService.generateToken(email, userId, role);

        assertNotNull(token);
        assertTrue(jwtService.isTokenValid(token));
        assertTrue(jwtService.isTokenValid(token, email));
        assertEquals(email, jwtService.extractEmail(token));
        assertEquals(userId, jwtService.extractUserId(token));
        assertEquals(role, jwtService.extractRole(token));
    }

    @Test
    void testInvalidTokenRejection() {
        String invalidToken = "invalid.jwt.token";
        assertFalse(jwtService.isTokenValid(invalidToken));
    }
}
