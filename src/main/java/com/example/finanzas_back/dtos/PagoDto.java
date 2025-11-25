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
public class PagoDto implements Serializable {
    private Long id_pago;
    private LocalDate fecha_pago;
    private Double monto;
    private String metodo_pago;
    private String referencia;

    private CreditoDto creditodto;
    private MonedaDto monedadto;
    private ClienteDto clientedto;
}
