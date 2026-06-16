package com.empresa.gestfy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.empresa.gestfy.models.Categoria;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByNomeIgnoreCase(String nome);
}
