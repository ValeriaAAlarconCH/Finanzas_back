package com.example.finanzas_back.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstadoCuentaDto implements Serializable {
    private CreditoDto credito;
    private LocalDate fechaCorte;
    private Double saldoCapital;
    private Double saldoInteres;
    private Double saldoMora;
    private Double totalDeuda;
    private List<CuotaDto> cuotasVencidas;
    private List<CuotaDto> cuotasPendientes;
    private List<PagoDto> pagosUltimoMes;
    private ResumenPagosDto resumen;
}
