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
public class ResumenPagosDto implements Serializable {
    private Integer totalCuotas;
    private Integer cuotasPagadas;
    private Integer cuotasPendientes;
    private Integer cuotasVencidas;
    private Double totalPagado;
    private Double totalPendiente;
    private Double porcentajeAvance;
}
