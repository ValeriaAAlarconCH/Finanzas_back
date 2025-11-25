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
public class IndicadorFinancieroDto implements Serializable {
    private Long id_indicador;
    private LocalDate fecha_calculo;
    private Double VAN;
    private Double TIR;
    private Double TCEA;
    private Double TREA;
    private Double duracion;
    private Double duracion_modificada;
    private Double convexidad;

    private CreditoDto creditodto;
}
