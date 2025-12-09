package com.empresa.gestfy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.empresa.gestfy.models.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
