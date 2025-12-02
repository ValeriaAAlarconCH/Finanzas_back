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
public class SimulacionCreditoDto implements Serializable {
    private Double montoPrincipal;
    private Double tasaAnual;
    private Integer plazoMeses;
    private Integer mesesGracia;
    private String tipoGracia; // "total" o "parcial"
    private String tipoTasa; // "nominal" o "efectiva"
    private Long idCapitalizacion;
    private Long idMoneda;
    private Double seguroDesgravamen; // porcentaje mensual
    private Double seguroInmueble; // porcentaje mensual
    private Double otrosCargos; // monto fijo mensual
    private LocalDate fechaPrimeraCuota;
    private Long idCliente;
    private Long idUnidad;
    private Long idEntidad;
}
