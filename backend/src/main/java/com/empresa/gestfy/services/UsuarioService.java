package com.empresa.gestfy.services;

import com.empresa.gestfy.models.Usuario;
import com.empresa.gestfy.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * UsuarioService
 * Responsável por operações de usuários (CRUD, autenticação, etc).
 * Separa a lógica de negócio da camada HTTP.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Criar novo usuário
     */
    public UsuarioDTO criar(UsuarioRequest request) {
        // Validar se email já existe
        if (usuarioRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }

        Usuario usuario = new Usuario(
                request.nome(),
                request.email(),
                request.senha(),
                request.perfil());

        Usuario salvo = usuarioRepository.save(usuario);
        return toDTO(salvo);
    }

    /**
     * Listar todos os usuários
     */
    public List<UsuarioDTO> listar() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar usuário por ID
     */
    public Optional<UsuarioDTO> buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .map(this::toDTO);
    }

    /**
     * Atualizar usuário
     */
    public UsuarioDTO atualizar(Long id, UsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setSenha(request.senha());
        usuario.setPerfil(request.perfil());

        Usuario atualizado = usuarioRepository.save(usuario);
        return toDTO(atualizado);
    }

    /**
     * Remover usuário
     */
    public void remover(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    /**
     * Helper: Converter modelo para DTO
     */
    private UsuarioDTO toDTO(Usuario usuario) {
        return new UsuarioDTO(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getPerfil());
    }

    // ===== DTOs =====
    public record UsuarioRequest(String nome, String email, String senha, String perfil) {}
    public record UsuarioDTO(Long id, String nome, String email, String perfil) {}
}
