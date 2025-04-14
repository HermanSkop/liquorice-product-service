package org.example.liqouriceproductservice.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
class JwtRoleConverterTest {

    private JwtRoleConverter converter;
    private Jwt jwt;

    @BeforeEach
    void setUp() {
        converter = new JwtRoleConverter();
        jwt = mock(Jwt.class);
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "test-subject");
        when(jwt.getClaims()).thenReturn(claims);
        when(jwt.getSubject()).thenReturn("test-subject");
        when(jwt.getExpiresAt()).thenReturn(Instant.now().plusSeconds(300));
        when(jwt.getIssuedAt()).thenReturn(Instant.now());
    }

    @Test
    void convert_WithNoRoles_ShouldCreateTokenWithEmptyAuthorities() {
        when(jwt.getClaimAsStringList("roles")).thenReturn(null);
        when(jwt.getClaimAsMap("realm_access")).thenReturn(null);

        AbstractAuthenticationToken result = converter.convert(jwt);

        assertNotNull(result);
        assertTrue(result.getAuthorities().isEmpty());
    }

    @Test
    void convert_ShouldUseSubject() {
        when(jwt.getClaimAsString("preferred_username")).thenReturn(null);
        when(jwt.getClaimAsStringList("roles")).thenReturn(Collections.singletonList("user"));

        AbstractAuthenticationToken result = converter.convert(jwt);

        assertNotNull(result);
        assertEquals("test-subject", result.getName());
    }
}