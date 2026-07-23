package com.empresa.gestfy.services;

import com.empresa.gestfy.repositories.ClienteRepository;
import com.empresa.gestfy.repositories.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    @Test
    void shouldAuthenticateDefaultAdminCredentials() {
        AuthenticationManager authenticationManager = Mockito.mock(AuthenticationManager.class);
        UserDetailsService userDetailsService = Mockito.mock(UserDetailsService.class);
        UsuarioRepository usuarioRepository = Mockito.mock(UsuarioRepository.class);
        ClienteRepository clienteRepository = Mockito.mock(ClienteRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("bad credentials"));
        when(userDetailsService.loadUserByUsername("admin@gestfy.com"))
                .thenReturn(new User("admin@gestfy.com", "{noop}dredenciasi", java.util.List.of()));

        AuthService authService = new AuthService(
                authenticationManager,
                userDetailsService,
                usuarioRepository,
                clienteRepository,
                passwordEncoder);

        assertDoesNotThrow(() -> {
            String token = authService.authenticate("admin@gestfy.com", "dredenciasi");
            assertNotNull(token);
        });
    }
}
