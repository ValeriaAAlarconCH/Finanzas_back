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
public class ResultadoPagoDto implements Serializable {
    private PagoDto pago;
    private List<PagoCuotaDto> aplicaciones;
    private Double montoAplicado;
    private Double montoExcedente;
    private String mensaje;
    private Boolean exito;
}
