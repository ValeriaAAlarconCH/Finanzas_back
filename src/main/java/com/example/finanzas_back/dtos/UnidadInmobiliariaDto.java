package com.example.finanzas_back.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UnidadInmobiliariaDto implements Serializable {
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

    private ProyectoInmobiliarioDto proyectodto;
    private MonedaDto monedadto;
}
