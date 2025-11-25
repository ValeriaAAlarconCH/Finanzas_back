package com.example.finanzas_back.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class ProyectoInmobiliario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_proyecto;

    private String nombre_proyecto;

    private String direccion;

    private String descripcion;

    private String desarrollador;

    private LocalDate fecha_inicio;

    private LocalDate fecha_entrega_estimada;
}
