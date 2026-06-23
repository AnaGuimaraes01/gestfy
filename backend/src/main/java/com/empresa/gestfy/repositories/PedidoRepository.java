package com.empresa.gestfy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.empresa.gestfy.models.Pedido;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
       @Query("SELECT DISTINCT p FROM Pedido p " +
                     "LEFT JOIN FETCH p.cliente " +
                     "LEFT JOIN FETCH p.itens i " +
                     "LEFT JOIN FETCH i.produto prod " +
                     "LEFT JOIN FETCH prod.categoria " +
                     "ORDER BY p.data DESC")
       List<Pedido> findAllWithRelationships();

       @Query("SELECT p FROM Pedido p " +
                     "LEFT JOIN FETCH p.cliente " +
                     "LEFT JOIN FETCH p.itens i " +
                     "LEFT JOIN FETCH i.produto prod " +
                     "LEFT JOIN FETCH prod.categoria " +
                     "WHERE p.id = :id")
       Optional<Pedido> findByIdWithRelationships(@Param("id") Long id);

       @Query("SELECT DISTINCT p FROM Pedido p " +
                     "LEFT JOIN FETCH p.cliente " +
                     "LEFT JOIN FETCH p.itens i " +
                     "LEFT JOIN FETCH i.produto prod " +
                     "LEFT JOIN FETCH prod.categoria " +
                     "WHERE p.status = :status " +
                     "ORDER BY p.data DESC")
       List<Pedido> findByStatusWithRelationships(@Param("status") String status);

       @Query("SELECT DISTINCT p FROM Pedido p " +
                     "LEFT JOIN FETCH p.cliente " +
                     "LEFT JOIN FETCH p.itens i " +
                     "LEFT JOIN FETCH i.produto prod " +
                     "LEFT JOIN FETCH prod.categoria " +
                     "WHERE p.data BETWEEN :dataInicio AND :dataFim " +
                     "ORDER BY p.data DESC")
                     
       List<Pedido> findByDataBetweenWithRelationships(
                     @Param("dataInicio") LocalDateTime dataInicio,
                     @Param("dataFim") LocalDateTime dataFim);

       List<Pedido> findByStatus(String status);
       List<Pedido> findByDataBetween(LocalDateTime dataInicio, LocalDateTime dataFim);
       List<Pedido> findByCliente_Id(Long clienteId);
       List<Pedido> findByStatusAndDataBetween(String status, LocalDateTime dataInicio, LocalDateTime dataFim);
}
