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
public class CuotaDto implements Serializable {
    private Long id_cuota;
    private Integer numero_cuota;
    private LocalDate fecha_vencimiento;
    private Integer dias_periodo;
    private Double saldo_inicial;
    private Double capital_programado;
    private Double interes_programado;
    private Double otros_cargos;
    private Double total_cuota;
    private Double saldo_final;
    private String estado;

    private CreditoDto creditodto;
}
