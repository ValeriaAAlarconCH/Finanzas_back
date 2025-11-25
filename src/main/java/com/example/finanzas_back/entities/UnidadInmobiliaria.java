package com.example.finanzas_back.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UnidadInmobiliaria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_unidad;

    private String codigo_unidad;

    private String tipo;

    private Double area_m2;

    private Integer num_dormitorios;

    private Integer num_banos;

    private String piso;

    private Double precio_lista;

    private Double precio_venta;

    private String estado;

    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "id_proyecto")
    private ProyectoInmobiliario proyecto;

    @ManyToOne
    @JoinColumn(name = "id_moneda")
    private Moneda moneda;
}
