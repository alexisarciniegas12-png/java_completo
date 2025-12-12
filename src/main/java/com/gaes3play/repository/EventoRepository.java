package com.gaes3play.repository;

import com.gaes3play.models.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findByTituloContainingIgnoreCase(String titulo);

}
