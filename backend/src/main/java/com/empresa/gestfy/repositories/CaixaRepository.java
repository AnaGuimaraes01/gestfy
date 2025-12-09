package com.empresa.gestfy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.empresa.gestfy.models.Caixa;

import java.time.LocalDate;
import java.util.Optional;

public interface CaixaRepository extends JpaRepository<Caixa, Long> {

    Optional<Caixa> findByData(LocalDate data);
}
