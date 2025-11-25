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
public class PagoCuotaDto implements Serializable {
    private Long idPago;
    private Long idCuota;
    private Double monto_aplicado;
    private LocalDate fecha_pago;
}
