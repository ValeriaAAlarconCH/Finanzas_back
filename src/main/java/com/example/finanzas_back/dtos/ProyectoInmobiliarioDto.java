package com.example.finanzas_back.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProyectoInmobiliarioDto implements Serializable {
    private Long id_proyecto;
    private String nombre_proyecto;
    private String direccion;
    private String descripcion;
    private String desarrollador;
    private LocalDate fecha_inicio;
    private LocalDate fecha_entrega_estimada;
}
