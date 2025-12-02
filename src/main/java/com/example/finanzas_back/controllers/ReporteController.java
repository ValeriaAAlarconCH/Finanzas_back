package com.example.finanzas_back.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.finanzas_back.dtos.*;
import com.example.finanzas_back.interfaces.IReporteService;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:4200",
        allowCredentials = "true",
        exposedHeaders = "Authorization")
@RequestMapping("/reportes")
public class ReporteController {

    @Autowired
    private IReporteService reporteService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDto> obtenerDashboard() {
        try {
            DashboardDto dashboard = reporteService.obtenerDashboard();
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            log.error("Error al obtener dashboard: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/creditos")
    public ResponseEntity<ReporteCreditosDto> generarReporteCreditos(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin) {
        try {
            LocalDate inicio = fechaInicio != null ? LocalDate.parse(fechaInicio) : LocalDate.now().minusMonths(1);
            LocalDate fin = fechaFin != null ? LocalDate.parse(fechaFin) : LocalDate.now();

            ReporteCreditosDto reporte = reporteService.generarReporteCreditos(inicio, fin);
            return ResponseEntity.ok(reporte);
        } catch (Exception e) {
            log.error("Error al generar reporte de créditos: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/pagos")
    public ResponseEntity<ReportePagosDto> generarReportePagos(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin) {
        try {
            LocalDate inicio = fechaInicio != null ? LocalDate.parse(fechaInicio) : LocalDate.now().minusMonths(1);
            LocalDate fin = fechaFin != null ? LocalDate.parse(fechaFin) : LocalDate.now();

            ReportePagosDto reporte = reporteService.generarReportePagos(inicio, fin);
            return ResponseEntity.ok(reporte);
        } catch (Exception e) {
            log.error("Error al generar reporte de pagos: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/kpis")
    public ResponseEntity<Map<String, Double>> obtenerKPIs() {
        try {
            Map<String, Double> kpis = reporteService.calcularKPIs();
            return ResponseEntity.ok(kpis);
        } catch (Exception e) {
            log.error("Error al calcular KPIs: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/exportar/creditos-excel")
    public ResponseEntity<byte[]> exportarReporteCreditosExcel(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin) {
        try {
            LocalDate inicio = fechaInicio != null ? LocalDate.parse(fechaInicio) : LocalDate.now().minusMonths(1);
            LocalDate fin = fechaFin != null ? LocalDate.parse(fechaFin) : LocalDate.now();

            byte[] excelBytes = reporteService.exportarReporteCreditosExcel(inicio, fin);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "reporte_creditos.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelBytes);
        } catch (Exception e) {
            log.error("Error al exportar reporte: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/exportar/estado-cuenta/{idCredito}/pdf")
    public ResponseEntity<byte[]> exportarEstadoCuentaPdf(@PathVariable Long idCredito) {
        try {
            byte[] pdfBytes = reporteService.exportarEstadoCuentaPdf(idCredito);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "estado_cuenta_" + idCredito + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            log.error("Error al exportar estado de cuenta: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/exportar/cronograma/{idCredito}/pdf")
    public ResponseEntity<byte[]> exportarCronogramaPdf(@PathVariable Long idCredito) {
        try {
            byte[] pdfBytes = reporteService.exportarCronogramaPdf(idCredito);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "cronograma_" + idCredito + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            log.error("Error al exportar cronograma: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
