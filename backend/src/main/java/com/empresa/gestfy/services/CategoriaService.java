package com.empresa.gestfy.services;

import com.empresa.gestfy.dto.categoria.CategoriaDTO;
import com.empresa.gestfy.dto.categoria.CategoriaRequest;
import com.empresa.gestfy.models.Categoria;
import com.empresa.gestfy.repositories.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * CategoriaService
 * Responsável por operações com categorias (CRUD, validações)
 */
@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    /**
     * Criar nova categoria
     */
    public CategoriaDTO criar(CategoriaRequest request) {
        Categoria categoria = new Categoria(request.nome());
        Categoria salva = categoriaRepository.save(categoria);
        return toDTO(salva);
    }

    /**
     * Listar todas as categorias
     */
    public List<CategoriaDTO> listar() {
        return categoriaRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar categoria por ID
     */
    public Optional<CategoriaDTO> buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .map(this::toDTO);
    }

    /**
     * Buscar modelo de categoria por ID (sem conversão para DTO)
     * Usado internamente por outros services
     */
    public Optional<Categoria> buscarCategoriaModelo(Long id) {
        return categoriaRepository.findById(id);
    }

    /**
     * Atualizar categoria
     */
    public CategoriaDTO atualizar(Long id, CategoriaRequest request) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        categoria.setNome(request.nome());
        Categoria atualizada = categoriaRepository.save(categoria);
        return toDTO(atualizada);
    }

    /**
     * Remover categoria
     */
    public void remover(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new RuntimeException("Categoria não encontrada");
        }
        categoriaRepository.deleteById(id);
    }

    /**
     * Converter modelo para DTO
     */
    private CategoriaDTO toDTO(Categoria categoria) {
        return new CategoriaDTO(categoria.getId(), categoria.getNome());
    }
}
