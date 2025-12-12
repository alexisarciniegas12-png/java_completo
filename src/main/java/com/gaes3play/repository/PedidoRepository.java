package com.gaes3play.repository;

import com.gaes3play.models.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByClienteContainingIgnoreCase(String cliente);

}
