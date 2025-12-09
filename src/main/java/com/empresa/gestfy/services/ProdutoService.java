package com.empresa.gestfy.services;

import com.empresa.gestfy.models.Produto;
import com.empresa.gestfy.repositories.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository repo;

    public ProdutoService(ProdutoRepository repo) {
        this.repo = repo;
    }

    public List<Produto> listarTodos() {
        return repo.findAll();
    }

    public Produto salvar(Produto produto) {
        return repo.save(produto);
    }
}
