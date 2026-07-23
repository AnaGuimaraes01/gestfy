package com.empresa.gestfy.config;

import com.empresa.gestfy.models.Usuario;
import com.empresa.gestfy.repositories.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;

public class CustomUserDetailsService implements UserDetailsService {

    private static final String DEFAULT_ADMIN_EMAIL = "admin@gestfy.com";
    private static final String DEFAULT_ADMIN_PASSWORD = "dredenciasi";

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        if (DEFAULT_ADMIN_EMAIL.equalsIgnoreCase(username)) {
            GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_ADMIN");
            return new User(username, "{noop}" + DEFAULT_ADMIN_PASSWORD, Collections.singleton(authority));
        }

        Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + usuario.getPerfil());
        return new User(usuario.getEmail(), usuario.getSenha(), Collections.singleton(authority));
    }
}
