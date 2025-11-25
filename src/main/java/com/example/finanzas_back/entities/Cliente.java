package com.example.finanzas_back.entities;

import com.example.finanzas_back.security.entities.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_cliente;

    private Integer dni;

    private String nombres;

    private String apellidos;

    private LocalDate fecha_nacimiento;

    private String sexo;

    private String direccion;

    private Integer telefono;

    private String email;

    private String ocupacion;

    private String empleador;

    private Double ingreso_mensual;

    private String estado_civil;

    private Integer num_dependientes;

    private String observaciones;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;
}
