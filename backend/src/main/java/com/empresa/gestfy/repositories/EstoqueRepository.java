package com.empresa.gestfy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.empresa.gestfy.models.Estoque;
import com.empresa.gestfy.models.Produto;

import java.util.List;


public interface EstoqueRepository extends JpaRepository<Estoque, Long> {
    List<Estoque> findByProduto(Produto produto);
}