package com.example.finanzas_back.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoValidacionDto implements Serializable {
    private Boolean valido;
    private List<String> errores;
    private List<String> advertencias;
    private String mensaje;
    private Integer puntaje; // Puntaje de riesgo (0-100)

    public ResultadoValidacionDto(Boolean valido, String mensaje) {
        this.valido = valido;
        this.mensaje = mensaje;
        this.errores = new ArrayList<>();
        this.advertencias = new ArrayList<>();
        this.puntaje = valido ? 0 : 100;
    }

    public void agregarError(String error) {
        if (errores == null) {
            errores = new ArrayList<>();
        }
        errores.add(error);
        valido = false;
    }

    public void agregarAdvertencia(String advertencia) {
        if (advertencias == null) {
            advertencias = new ArrayList<>();
        }
        advertencias.add(advertencia);
    }
}
