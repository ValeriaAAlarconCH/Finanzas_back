package com.example.finanzas_back.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReporteCreditosDto implements Serializable {
    private LocalDate fechaGeneracion;
    private LocalDate periodoInicio;
    private LocalDate periodoFin;
    private Integer totalCreditos;
    private Integer creditosAprobados;
    private Integer creditosRechazados;
    private Integer creditosActivos;
    private Integer creditosCancelados;
    private Double montoTotalDesembolsado;
    private Double montoTotalRecuperado;
    private Double tasaAprobacion;
    private List<CreditoDto> creditosRecientes;
    private Map<String, Integer> creditosPorEntidad;
    private Map<String, Double> montosPorMoneda;
    private List<TrendDataDto> tendenciaMensual;
}
