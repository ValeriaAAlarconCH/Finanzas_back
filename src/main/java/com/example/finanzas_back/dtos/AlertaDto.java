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
public class AlertaDto implements Serializable {
    private String tipo; // "vencimiento", "mora", "cumplimiento", "sistema"
    private String mensaje;
    private String prioridad; // "alta", "media", "baja"
    private LocalDate fecha;
    private Long referenciaId; // ID de crédito, cuota, etc.
    private Boolean leida;
}
