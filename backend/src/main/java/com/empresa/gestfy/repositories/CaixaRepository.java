package com.empresa.gestfy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.empresa.gestfy.models.Caixa;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CaixaRepository extends JpaRepository<Caixa, Long> {

    // Buscar todos os registros de uma data específica
    List<Caixa> findByData(LocalDate data);

    // Buscar registros de ENTRADA de uma data (pedidos pagos)
    List<Caixa> findByDataAndTipo(LocalDate data, String tipo);

    // Buscar o fechamento do dia (tipo FECHAMENTO)
    Optional<Caixa> findByDataAndTipoAndStatus(LocalDate data, String tipo, String status);

    // Buscar caixa aberto do dia
    Optional<Caixa> findByDataAndStatus(LocalDate data, String status);

    // Buscar todas as entradas de uma data
    List<Caixa> findByDataAndTipoOrderByHorarioAberturaAsc(LocalDate data, String tipo);
}