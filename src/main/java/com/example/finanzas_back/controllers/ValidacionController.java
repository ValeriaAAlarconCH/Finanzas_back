package com.example.finanzas_back.controllers;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.finanzas_back.dtos.*;
import com.example.finanzas_back.interfaces.IValidacionService;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:4200",
        allowCredentials = "true",
        exposedHeaders = "Authorization")
@RequestMapping("/validaciones")
public class ValidacionController {

    @Autowired
    private IValidacionService validacionService;

    @PostMapping("/cliente")
    public ResponseEntity<ResultadoValidacionDto> validarCliente(@RequestBody ClienteDto cliente) {
        try {
            ResultadoValidacionDto resultado = validacionService.validarCliente(cliente);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Error validando cliente: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/capacidad-pago")
    public ResponseEntity<ResultadoValidacionDto> validarCapacidadPago(
            @RequestBody ValidacionCapacidadRequest request) {
        try {
            ResultadoValidacionDto resultado = validacionService.validarCapacidadPago(
                    request.getCliente(), request.getMontoCuota());
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Error validando capacidad de pago: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/historial-crediticio/{idCliente}")
    public ResponseEntity<ResultadoValidacionDto> validarHistorialCrediticio(@PathVariable Long idCliente) {
        try {
            ResultadoValidacionDto resultado = validacionService.validarHistorialCrediticio(idCliente);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Error validando historial crediticio: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/credito")
    public ResponseEntity<ResultadoValidacionDto> validarCredito(@RequestBody CreditoDto credito) {
        try {
            ResultadoValidacionDto resultado = validacionService.validarCredito(credito);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Error validando crédito: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/unidad-inmobiliaria/{idUnidad}")
    public ResponseEntity<ResultadoValidacionDto> validarUnidadInmobiliaria(@PathVariable Long idUnidad) {
        try {
            ResultadoValidacionDto resultado = validacionService.validarUnidadInmobiliaria(idUnidad);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Error validando unidad inmobiliaria: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/documentos-cliente/{idCliente}")
    public ResponseEntity<ResultadoValidacionDto> validarDocumentosCliente(@PathVariable Long idCliente) {
        try {
            ResultadoValidacionDto resultado = validacionService.validarDocumentosCliente(idCliente);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Error validando documentos: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/completitud-solicitud")
    public ResponseEntity<ResultadoValidacionDto> validarCompletitudSolicitud(@RequestBody CreditoDto credito) {
        try {
            ResultadoValidacionDto resultado = validacionService.validarCompletitudSolicitud(credito);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Error validando completitud: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/completa")
    public ResponseEntity<ResultadoValidacionCompletaDto> validacionCompleta(@RequestBody CreditoDto credito) {
        try {
            // Ejecutar todas las validaciones
            ResultadoValidacionDto validacionCliente = validacionService.validarCliente(credito.getClientedto());
            ResultadoValidacionDto validacionCapacidad = validacionService.validarCapacidadPago(
                    credito.getClientedto(), calcularCuotaEstimada(credito));
            ResultadoValidacionDto validacionHistorial = validacionService.validarHistorialCrediticio(
                    credito.getClientedto().getId_cliente());
            ResultadoValidacionDto validacionCredito = validacionService.validarCredito(credito);
            ResultadoValidacionDto validacionUnidad = validacionService.validarUnidadInmobiliaria(
                    credito.getUnidaddto().getId_unidad());
            ResultadoValidacionDto validacionDocumentos = validacionService.validarDocumentosCliente(
                    credito.getClientedto().getId_cliente());
            ResultadoValidacionDto validacionCompletitud = validacionService.validarCompletitudSolicitud(credito);

            // Combinar resultados
            ResultadoValidacionCompletaDto resultadoCompleto = new ResultadoValidacionCompletaDto();
            resultadoCompleto.setValidacionCliente(validacionCliente);
            resultadoCompleto.setValidacionCapacidad(validacionCapacidad);
            resultadoCompleto.setValidacionHistorial(validacionHistorial);
            resultadoCompleto.setValidacionCredito(validacionCredito);
            resultadoCompleto.setValidacionUnidad(validacionUnidad);
            resultadoCompleto.setValidacionDocumentos(validacionDocumentos);
            resultadoCompleto.setValidacionCompletitud(validacionCompletitud);

            // Calcular puntaje total
            Integer puntajeTotal = validacionCliente.getPuntaje() + validacionCapacidad.getPuntaje() +
                    validacionHistorial.getPuntaje() + validacionCredito.getPuntaje() +
                    validacionUnidad.getPuntaje() + validacionDocumentos.getPuntaje();
            resultadoCompleto.setPuntajeTotal(puntajeTotal);

            // Determinar si es aprobado
            boolean esAprobado = validacionCliente.getValido() && validacionCapacidad.getValido() &&
                    validacionCredito.getValido() && validacionUnidad.getValido() &&
                    validacionDocumentos.getValido() && puntajeTotal < 70;

            resultadoCompleto.setAprobado(esAprobado);
            resultadoCompleto.setMensaje(esAprobado ?
                    "Crédito aprobado - Puntaje de riesgo: " + puntajeTotal :
                    "Crédito no aprobado - Puntaje de riesgo: " + puntajeTotal);

            return ResponseEntity.ok(resultadoCompleto);

        } catch (Exception e) {
            log.error("Error en validación completa: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // DTO para request específico
    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidacionCapacidadRequest {
        private ClienteDto cliente;
        private Double montoCuota;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultadoValidacionCompletaDto {
        private ResultadoValidacionDto validacionCliente;
        private ResultadoValidacionDto validacionCapacidad;
        private ResultadoValidacionDto validacionHistorial;
        private ResultadoValidacionDto validacionCredito;
        private ResultadoValidacionDto validacionUnidad;
        private ResultadoValidacionDto validacionDocumentos;
        private ResultadoValidacionDto validacionCompletitud;
        private Integer puntajeTotal;
        private Boolean aprobado;
        private String mensaje;
    }

    private Double calcularCuotaEstimada(CreditoDto credito) {
        // Cálculo simplificado de cuota
        Double tasaMensual = credito.getTasa_anual() / 12 / 100;
        Integer plazo = credito.getPlazo_meses();
        Double monto = credito.getMonto_principal();

        if (tasaMensual == 0) {
            return monto / plazo;
        }

        Double factor = Math.pow(1 + tasaMensual, plazo);
        return monto * (tasaMensual * factor) / (factor - 1);
    }
}
