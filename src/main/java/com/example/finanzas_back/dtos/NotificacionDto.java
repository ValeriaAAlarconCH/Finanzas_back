package com.example.finanzas_back.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionDto implements Serializable {
    private Long idNotificacion;
    private String tipo; // "email", "sistema", "recordatorio"
    private String destinatario; // email o usuario
    private String asunto;
    private String mensaje;
    private LocalDateTime fechaEnvio;
    private Boolean enviada;
    private Boolean leida;
    private Long referenciaId; // ID de crédito, cuota, etc.
    private String prioridad; // "alta", "media", "baja"
}
