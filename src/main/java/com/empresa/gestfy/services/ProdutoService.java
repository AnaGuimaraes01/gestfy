package com.empresa.gestfy.services;

import com.empresa.gestfy.models.Produto;
import com.empresa.gestfy.repositories.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    private final ProdutoRepository repository;

    public ProdutoService(ProdutoRepository repository) {
        this.repository = repository;
    }

    // LISTAR TODOS
    public List<Produto> listarTodos() {
        return repository.findAll();
    }

    // BUSCAR POR ID
    public Optional<Produto> buscarPorId(Long id) {
        return repository.findById(id);
    }

    // SALVAR / ATUALIZAR
    public Produto salvar(Produto produto) {
        return repository.save(produto);
    }

    // DELETAR
    public void deletar(Long id) {
        repository.deleteById(id);
    }
}
