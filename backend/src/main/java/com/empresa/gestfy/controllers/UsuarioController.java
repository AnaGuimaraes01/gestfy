package com.empresa.gestfy.controllers;

import com.empresa.gestfy.models.Usuario;
import com.empresa.gestfy.repositories.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // =========================
    // CRIAR USUÁRIO
    // =========================
    @PostMapping
    public ResponseEntity<UsuarioDTO> criarUsuario(@RequestBody @Valid UsuarioRequest request) {
        // Validar se email já existe
        if (usuarioRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }

        Usuario usuario = new Usuario(
                request.nome(),
                request.email(),
                request.senha(),
                request.perfil());

        usuario = usuarioRepository.save(usuario);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new UsuarioDTO(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getPerfil()));
    }

    // =========================
    // LISTAR TODOS OS USUÁRIOS
    // =========================
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        List<UsuarioDTO> usuarios = usuarioRepository.findAll()
                .stream()
                .map(u -> new UsuarioDTO(u.getId(), u.getNome(), u.getEmail(), u.getPerfil()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(usuarios);
    }

    // =========================
    // BUSCAR USUÁRIO POR ID
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable Long id) {
        return usuarioRepository.findById(id)
                .map(u -> ResponseEntity.ok(new UsuarioDTO(u.getId(), u.getNome(), u.getEmail(), u.getPerfil())))
                .orElse(ResponseEntity.notFound().build());
    }

    // =========================
    // ATUALIZAR USUÁRIO
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizarUsuario(
            @PathVariable Long id,
            @RequestBody @Valid UsuarioRequest request) {

        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setNome(request.nome());
                    usuario.setEmail(request.email());
                    usuario.setSenha(request.senha());
                    usuario.setPerfil(request.perfil());

                    usuario = usuarioRepository.save(usuario);

                    return ResponseEntity.ok(
                            new UsuarioDTO(usuario.getId(), usuario.getNome(), usuario.getEmail(),
                                    usuario.getPerfil()));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // =========================
    // REMOVER USUÁRIO
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerUsuario(@PathVariable Long id) {
        if (!usuarioRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        usuarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // DTOs
    public record UsuarioRequest(
            String nome,
            String email,
            String senha,
            String perfil) {
    }

    public record UsuarioDTO(
            Long id,
            String nome,
            String email,
            String perfil) {
    }
}
