package com.empresa.gestfy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.empresa.gestfy.models.Produto;
import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    // Busca parcial por nome do produto (case-insensitive)
    List<Produto> findByNomeContainingIgnoreCase(String nome);
}