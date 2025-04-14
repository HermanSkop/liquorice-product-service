package org.example.liqouriceproductservice.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.authentication.AuthenticationManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Autowired
    private SecurityConfig securityConfig;

    @Test
    void securityFilterChain_ShouldBeConfiguredCorrectly() {
        HttpSecurity http = mock(HttpSecurity.class);
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);

        assertDoesNotThrow(() -> securityConfig.securityFilterChain(http, authenticationManager));
    }

    @Test
    void jwtDecoder_ShouldBeConfigured() {
        JwtDecoder decoder = securityConfig.jwtDecoder();

        assertNotNull(decoder);
    }
}