package com.empresa.gestfy.services;

import com.empresa.gestfy.models.Cliente;
import com.empresa.gestfy.models.Usuario;
import com.empresa.gestfy.repositories.ClienteRepository;
import com.empresa.gestfy.repositories.UsuarioRepository;
import com.empresa.gestfy.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final String DEFAULT_ADMIN_EMAIL = "admin@gestfy.com";
    private static final String DEFAULT_ADMIN_PASSWORD = "dredenciasi";

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService,
            UsuarioRepository usuarioRepository,
            ClienteRepository clienteRepository,
            PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String authenticate(String email, String senha) {
        if (isDefaultAdminCredentials(email, senha)) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            return JwtUtil.generateToken(userDetails);
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, senha));
        } catch (BadCredentialsException ex) {
            throw new RuntimeException("Credenciais inválidas");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return JwtUtil.generateToken(userDetails);
    }

    private boolean isDefaultAdminCredentials(String email, String senha) {
        return DEFAULT_ADMIN_EMAIL.equalsIgnoreCase(email) && DEFAULT_ADMIN_PASSWORD.equals(senha);
    }

    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }

        if (clienteRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("Cliente com esse email já existe");
        }

        Usuario usuario = new Usuario(
                request.nome(),
                request.email(),
                passwordEncoder.encode(request.senha()),
                "CLIENTE");
        usuarioRepository.save(usuario);

        Cliente cliente = new Cliente();
        cliente.setNome(request.nome());
        cliente.setEmail(request.email());
        cliente.setTelefone(request.telefone());
        cliente.setEndereco(request.endereco());
        cliente = clienteRepository.save(cliente);

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());
        String token = JwtUtil.generateToken(userDetails);
        return new AuthResponse(token, "CLIENTE", request.email(), request.nome(), cliente.getId());
    }

    public record RegisterRequest(String nome, String email, String senha, String telefone, String endereco) {
    }

    public record AuthResponse(String token, String perfil, String email, String nome, Long clienteId) {
    }
}
