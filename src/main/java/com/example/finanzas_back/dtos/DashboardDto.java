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
    private Integer totalClientes;
    private Integer totalCreditos;
    private Integer creditosActivos;
    private Double carteraVigente;
    private Double carteraVencida;

    private Integer pagosHoy;
    private Double montoPagadoHoy;
    private Integer cuotasVencidasHoy;
    private Double montoVencidoHoy;

    private List<AlertaDto> alertas;

    private Map<String, Double> distribucionCartera;
    private List<MetricaMensualDto> tendenciaCobranza;
    private List<MetricaMensualDto> tendenciaCreditos;

    private Double indiceMorosidad;
    private Double eficienciaCobranza;
    private Double tasaCancelacion;
    private Double rotacionCartera;
}
