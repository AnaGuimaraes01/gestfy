package com.empresa.gestfy.repositories;

import com.empresa.gestfy.models.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
