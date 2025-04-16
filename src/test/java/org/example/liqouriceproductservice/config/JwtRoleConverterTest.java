package org.example.liqouriceproductservice.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class JwtRoleConverterTest {

    private JwtRoleConverter converter;

    @Mock
    private Jwt jwt;

    @BeforeEach
    void setUp() {
        converter = new JwtRoleConverter();

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "test-subject");
        lenient().when(jwt.getClaims()).thenReturn(claims);
        lenient().when(jwt.getSubject()).thenReturn("test-subject");
        lenient().when(jwt.getExpiresAt()).thenReturn(Instant.now().plusSeconds(300));
        lenient().when(jwt.getIssuedAt()).thenReturn(Instant.now());
    }

    @Test
    void convert_WithNoRoles_ShouldCreateTokenWithEmptyAuthorities() {
        lenient().when(jwt.getClaimAsStringList("roles")).thenReturn(null);
        lenient().when(jwt.getClaimAsMap("realm_access")).thenReturn(null);

        AbstractAuthenticationToken result = converter.convert(jwt);

        assertNotNull(result);
        assertTrue(result.getAuthorities().isEmpty());
    }

    @Test
    void convert_ShouldUseSubject() {
        lenient().when(jwt.getClaimAsString("preferred_username")).thenReturn(null);
        lenient().when(jwt.getClaimAsStringList("roles")).thenReturn(Collections.singletonList("user"));

        AbstractAuthenticationToken result = converter.convert(jwt);

        assertNotNull(result);
        assertEquals("test-subject", result.getName());
    }
}