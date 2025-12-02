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
public class DashboardDto implements Serializable {
    // Resumen general
    private Integer totalClientes;
    private Integer totalCreditos;
    private Integer creditosActivos;
    private Double carteraVigente;
    private Double carteraVencida;

    // Métricas del día
    private Integer pagosHoy;
    private Double montoPagadoHoy;
    private Integer cuotasVencidasHoy;
    private Double montoVencidoHoy;

    // Alertas
    private List<AlertaDto> alertas;

    // Gráficos datos
    private Map<String, Double> distribucionCartera;
    private List<MetricaMensualDto> tendenciaCobranza; // CORREGIDO: usa MetricaMensualDto
    private List<MetricaMensualDto> tendenciaCreditos; // CORREGIDO: usa MetricaMensualDto

    // KPIs
    private Double indiceMorosidad;
    private Double eficienciaCobranza;
    private Double tasaCancelacion;
    private Double rotacionCartera;
}
