package com.empresa.gestfy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.empresa.gestfy.models.PedidoItem;

public interface PedidoItemRepository extends JpaRepository<PedidoItem, Long> {
}
