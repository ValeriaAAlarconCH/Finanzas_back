package com.example.finanzas_back.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasClienteDto implements Serializable {
    private ClienteDto cliente;
    private Integer totalCreditos;
    private Integer creditosActivos;
    private Integer creditosCancelados;
    private Double montoTotalPrestado;
    private Double montoTotalPagado;
    private Double saldoPendiente;
    private Double porcentajePago;
    private Integer cuotasPagadas;
    private Integer cuotasPendientes;
    private Integer cuotasVencidas;
    private List<CreditoDto> historialCreditos;
    private List<PagoDto> ultimosPagos;
}
