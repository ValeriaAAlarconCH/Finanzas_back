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
public class MonedaDto implements Serializable {
    private Long id_moneda;
    private String codigo;
    private String nombre;
    private String simbolo;
}
