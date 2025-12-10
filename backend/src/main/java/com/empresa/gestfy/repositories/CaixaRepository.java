package com.empresa.gestfy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.empresa.gestfy.models.Caixa;

import java.time.LocalDate;

public interface CaixaRepository extends JpaRepository<Caixa, Long> {

    Caixa findByData(LocalDate data);
}