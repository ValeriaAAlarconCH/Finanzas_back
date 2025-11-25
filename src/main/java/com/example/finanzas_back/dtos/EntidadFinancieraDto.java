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
public class EntidadFinancieraDto implements Serializable {
    private Long id_entidad;
    private String nombre;
    private String codigo_autorizacion;
    private Integer ruc;
    private String direccion;
    private Integer telefono;
    private String email;
}
