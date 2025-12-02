package com.example.finanzas_back.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasProyectoDto implements Serializable {
    private ProyectoInmobiliarioDto proyecto;
    private Integer totalUnidades;
    private Integer unidadesVendidas;
    private Integer unidadesDisponibles;
    private Double valorTotalProyecto;
    private Double valorVendido;
    private Double valorDisponible;
    private Integer creditosAsociados;
    private Double montoFinanciado;
    private Double tasaVenta;
    private List<UnidadInmobiliariaDto> unidadesMasVendidas;
    private Map<String, Integer> ventasPorMes;
}
