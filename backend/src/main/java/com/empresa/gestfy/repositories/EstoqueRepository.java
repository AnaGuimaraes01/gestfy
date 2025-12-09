package com.empresa.gestfy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.empresa.gestfy.models.Estoque;

public interface EstoqueRepository extends JpaRepository<Estoque, Long> {
}
