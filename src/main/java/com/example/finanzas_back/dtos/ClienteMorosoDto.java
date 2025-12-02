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
public class ClienteMorosoDto implements Serializable {
    private Long idCliente;
    private String nombreCompleto;
    private Double deudaTotal;
    private Integer diasMora;
    private Integer cuotasVencidas;
}
