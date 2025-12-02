package com.example.finanzas_back.interfaces;

import com.example.finanzas_back.dtos.NotificacionDto;
import com.example.finanzas_back.entities.Cuota;
import com.example.finanzas_back.entities.Credito;

import java.time.LocalDate;
import java.util.List;

public interface INotificacionService {

    // Notificaciones por correo
    void enviarNotificacionVencimiento(Cuota cuota);

    void enviarNotificacionPagoConfirmado(Cuota cuota);

    void enviarNotificacionCreditoAprobado(Credito credito);

    void enviarNotificacionRecordatorio(LocalDate fecha);

    // Notificaciones internas
    void crearNotificacionSistema(String tipo, String mensaje, Long referenciaId);

    // Gestión de notificaciones
    List<NotificacionDto> obtenerNotificacionesPendientes();

    void marcarComoLeida(Long idNotificacion);

    void eliminarNotificacion(Long idNotificacion);

    // Programación de notificaciones
    void programarNotificacionesDiarias();

    void programarNotificacionesMensuales();
}
