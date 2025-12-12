package com.gaes3play.repository;

import com.gaes3play.models.Reservacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReservacionRepository extends JpaRepository<Reservacion, Long> {
    List<Reservacion> findByClienteNombreContainingIgnoreCase(String clienteNombre);
}
