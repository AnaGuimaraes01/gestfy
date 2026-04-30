package com.empresa.gestfy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.empresa.gestfy.models.Estoque;
import java.util.List;


public interface EstoqueRepository extends JpaRepository<Estoque, Long> {
    // Buscar movimentações por ID do produto
    List<Estoque> findByProduto_Id(Long produtoId);
}