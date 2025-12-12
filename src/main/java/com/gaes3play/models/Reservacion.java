package com.gaes3play.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Table(name = "reservaciones")
public class Reservacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nueva propiedad ClienteNombre
    private String clienteNombre;

    // Nueva propiedad pax (Número de personas)
    private Integer pax;

    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    private Estado estado;

    // Nueva propiedad tipoEvento (Tipo de evento)
    private String tipoEvento;

    public static enum Estado {
        Pendiente,
        Confirmada,
        Cancelada
    }
}
