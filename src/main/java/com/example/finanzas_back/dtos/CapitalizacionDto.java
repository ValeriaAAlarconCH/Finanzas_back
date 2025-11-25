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
public class CapitalizacionDto implements Serializable {
    private Long id_capitalizacion;
    private String nombre;
    private Double periodos_por_ano;
}
