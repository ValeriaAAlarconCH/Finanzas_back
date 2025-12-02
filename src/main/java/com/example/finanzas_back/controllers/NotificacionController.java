package com.example.finanzas_back.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.finanzas_back.dtos.NotificacionDto;
import com.example.finanzas_back.interfaces.INotificacionService;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:4200",
        allowCredentials = "true",
        exposedHeaders = "Authorization")
@RequestMapping("/notificaciones")
public class NotificacionController {

    @Autowired
    private INotificacionService notificacionService;

    @GetMapping("/pendientes")
    public ResponseEntity<List<NotificacionDto>> obtenerNotificacionesPendientes() {
        try {
            List<NotificacionDto> notificaciones = notificacionService.obtenerNotificacionesPendientes();
            return ResponseEntity.ok(notificaciones);
        } catch (Exception e) {
            log.error("Error obteniendo notificaciones: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/marcar-leida/{id}")
    public ResponseEntity<String> marcarComoLeida(@PathVariable Long id) {
        try {
            notificacionService.marcarComoLeida(id);
            return ResponseEntity.ok("Notificación marcada como leída");
        } catch (Exception e) {
            log.error("Error marcando notificación: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error marcando notificación");
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminarNotificacion(@PathVariable Long id) {
        try {
            notificacionService.eliminarNotificacion(id);
            return ResponseEntity.ok("Notificación eliminada");
        } catch (Exception e) {
            log.error("Error eliminando notificación: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error eliminando notificación");
        }
    }

    @PostMapping("/enviar-recordatorios")
    public ResponseEntity<String> enviarRecordatorios(@RequestParam(required = false) String fecha) {
        try {
            LocalDate fechaRecordatorio = fecha != null ? LocalDate.parse(fecha) : LocalDate.now();
            notificacionService.enviarNotificacionRecordatorio(fechaRecordatorio);
            return ResponseEntity.ok("Recordatorios enviados exitosamente");
        } catch (Exception e) {
            log.error("Error enviando recordatorios: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error enviando recordatorios");
        }
    }

    @PostMapping("/procesar-diarias")
    public ResponseEntity<String> procesarNotificacionesDiarias() {
        try {
            notificacionService.programarNotificacionesDiarias();
            return ResponseEntity.ok("Notificaciones diarias procesadas");
        } catch (Exception e) {
            log.error("Error procesando notificaciones diarias: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error procesando notificaciones");
        }
    }

    @PostMapping("/procesar-mensuales")
    public ResponseEntity<String> procesarNotificacionesMensuales() {
        try {
            notificacionService.programarNotificacionesMensuales();
            return ResponseEntity.ok("Notificaciones mensuales procesadas");
        } catch (Exception e) {
            log.error("Error procesando notificaciones mensuales: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error procesando notificaciones");
        }
    }
}