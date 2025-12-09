package com.empresa.gestfy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.empresa.gestfy.models.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
