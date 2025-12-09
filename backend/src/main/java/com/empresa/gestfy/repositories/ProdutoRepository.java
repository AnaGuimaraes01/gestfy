package com.empresa.gestfy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.empresa.gestfy.models.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}