package com.example.finanzas_back.controllers;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.finanzas_back.dtos.*;
import com.example.finanzas_back.interfaces.IPagoManagementService;
import com.example.finanzas_back.interfaces.IPagoService;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:4200",
        allowCredentials = "true",
        exposedHeaders = "Authorization")
@RequestMapping("/pagos")
public class PagoManagementController {

    @Autowired
    private IPagoManagementService pagoManagementService;

    @Autowired
    private IPagoService pagoService;

    @PostMapping("/registrar-con-aplicacion")
    public ResponseEntity<ResultadoPagoDto> registrarPagoConAplicacion(
            @RequestBody RegistroPagoRequest request) {
        try {
            log.info("Registrando pago con aplicación a cuotas");

            ResultadoPagoDto resultado = pagoManagementService.registrarPago(
                    request.getPago(),
                    request.getIdsCuotas()
            );

            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Error al registrar pago: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/calcular-mora")
    public ResponseEntity<Double> calcularMora(
            @RequestBody CalculoMoraRequest request) {
        try {
            Double mora = pagoManagementService.calcularMora(
                    request.getCuota(),
                    request.getFechaPago()
            );

            return ResponseEntity.ok(mora);
        } catch (Exception e) {
            log.error("Error al calcular mora: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(0.0);
        }
    }

    @GetMapping("/estado-cuenta/{idCredito}")
    public ResponseEntity<EstadoCuentaDto> generarEstadoCuenta(
            @PathVariable Long idCredito,
            @RequestParam(required = false) LocalDate fechaCorte) {
        try {
            if (fechaCorte == null) {
                fechaCorte = LocalDate.now();
            }

            EstadoCuentaDto estadoCuenta = pagoManagementService.generarEstadoCuenta(
                    idCredito, fechaCorte);

            return ResponseEntity.ok(estadoCuenta);
        } catch (Exception e) {
            log.error("Error al generar estado de cuenta: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/verificar-capacidad-pago")
    public ResponseEntity<Boolean> verificarCapacidadPago(
            @RequestBody VerificacionCapacidadRequest request) {
        try {
            Boolean capacidad = pagoManagementService.verificarCapacidadPago(
                    request.getIdCliente(),
                    request.getMontoCuota()
            );

            return ResponseEntity.ok(capacidad);
        } catch (Exception e) {
            log.error("Error al verificar capacidad de pago: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(false);
        }
    }

    @GetMapping("/cuota-maxima/{idCliente}")
    public ResponseEntity<Double> calcularCuotaMaxima(
            @PathVariable Long idCliente,
            @RequestParam(required = false) Double porcentajeEndeudamiento) {
        try {
            // Obtener cliente para conocer su ingreso
            // Esto debería estar en otro servicio, por simplicidad lo calculamos aquí
            // En producción, se debería inyectar un servicio de cliente

            // Por ahora, usamos un valor fijo para la demostración
            Double ingresoMensual = 5000.0; // Valor de ejemplo

            if (porcentajeEndeudamiento == null) {
                porcentajeEndeudamiento = 0.40; // 40%
            }

            Double cuotaMaxima = pagoManagementService.calcularCuotaMaxima(
                    ingresoMensual, porcentajeEndeudamiento);

            return ResponseEntity.ok(cuotaMaxima);
        } catch (Exception e) {
            log.error("Error al calcular cuota máxima: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(0.0);
        }
    }

    @PostMapping("/recalcular-cronograma")
    public ResponseEntity<List<CuotaDto>> recalcularCronograma(
            @RequestBody RecalculoCronogramaRequest request) {
        try {
            List<CuotaDto> cronogramaRecalculado =
                    pagoManagementService.recalcularCronogramaDespuesPago(
                            request.getIdCredito(),
                            request.getMontoAdelantado()
                    );

            return ResponseEntity.ok(cronogramaRecalculado);
        } catch (Exception e) {
            log.error("Error al recalcular cronograma: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    // DTOs para requests específicos
    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegistroPagoRequest {
        private PagoDto pago;
        private List<Long> idsCuotas;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CalculoMoraRequest {
        private CuotaDto cuota;
        private LocalDate fechaPago;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VerificacionCapacidadRequest {
        private Long idCliente;
        private Double montoCuota;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecalculoCronogramaRequest {
        private Long idCredito;
        private Double montoAdelantado;
    }
}
