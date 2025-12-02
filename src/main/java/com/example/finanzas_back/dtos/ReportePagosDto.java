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
public class ReportePagosDto implements Serializable {
    private LocalDate fechaGeneracion;
    private LocalDate periodoInicio;
    private LocalDate periodoFin;
    private Integer totalPagos;
    private Double montoTotalPagado;
    private Double promedioPago;
    private Integer pagosPuntuales;
    private Integer pagosConMora;
    private Double tasaCobranza;
    private List<PagoDto> pagosRecientes;
    private Map<String, Integer> pagosPorMetodo;
    private Map<String, Double> pagosPorDia;
    private List<TopClienteDto> topClientesPagos; // CORREGIDO: usa TopClienteDto
}
