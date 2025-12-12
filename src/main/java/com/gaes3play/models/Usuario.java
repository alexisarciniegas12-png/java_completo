package com.gaes3play.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Table(name = "usuarios")
public class Usuario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String correo;

    @Column(name = "contrasena")
    private String contrasena;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    public static enum Rol {
        Administrador, Empleado, Cliente
    }
}
