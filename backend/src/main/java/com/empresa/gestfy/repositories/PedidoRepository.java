package com.empresa.gestfy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.empresa.gestfy.models.Pedido;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    // Buscar pedidos por status
    List<Pedido> findByStatus(String status);
    
    // Buscar pedidos por data
    List<Pedido> findByDataBetween(LocalDateTime dataInicio, LocalDateTime dataFim);
    
    // Buscar pedidos por cliente ID
    List<Pedido> findByCliente_Id(Long clienteId);
    
    // Buscar pedidos finalizados de um dia específico
    List<Pedido> findByStatusAndDataBetween(String status, LocalDateTime dataInicio, LocalDateTime dataFim);
}
