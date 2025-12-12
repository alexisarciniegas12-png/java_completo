package com.gaes3play.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cliente;     // Nombre del cliente
    private String descripcion; // Descripción del pedido
    private Double precio;      // Precio del pedido

    private Long usuarioId;     // ID del usuario que hizo el pedido

    // No es necesario los campos "fecha" y "estado" que se eliminaron
}
