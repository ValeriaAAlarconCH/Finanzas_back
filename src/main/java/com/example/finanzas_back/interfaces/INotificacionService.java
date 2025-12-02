package com.example.finanzas_back.interfaces;

import com.example.finanzas_back.dtos.NotificacionDto;
import com.example.finanzas_back.entities.Cuota;
import com.example.finanzas_back.entities.Credito;

import java.time.LocalDate;
import java.util.List;

public interface INotificacionService {
    void enviarNotificacionVencimiento(Cuota cuota);

    void enviarNotificacionPagoConfirmado(Cuota cuota);

    void enviarNotificacionCreditoAprobado(Credito credito);

    void enviarNotificacionRecordatorio(LocalDate fecha);

    void crearNotificacionSistema(String tipo, String mensaje, Long referenciaId);

    List<NotificacionDto> obtenerNotificacionesPendientes();

    void marcarComoLeida(Long idNotificacion);

    void eliminarNotificacion(Long idNotificacion);

    void programarNotificacionesDiarias();

    void programarNotificacionesMensuales();
}
