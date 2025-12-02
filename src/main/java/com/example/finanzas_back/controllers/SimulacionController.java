package com.example.finanzas_back.controllers;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.finanzas_back.dtos.*;
import com.example.finanzas_back.interfaces.ICalculoFinancieroService;
import com.example.finanzas_back.interfaces.ICreditoService;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:4200",
        allowCredentials = "true",
        exposedHeaders = "Authorization")
@RequestMapping("/simulaciones")
public class SimulacionController {

    @Autowired
    private ICalculoFinancieroService calculoFinancieroService;

    @Autowired
    private ICreditoService creditoService;

    @PostMapping("/generar-cronograma")
    public ResponseEntity<List<CuotaDto>> generarCronograma(@RequestBody CreditoDto creditoDto) {
        try {
            log.info("Generando cronograma para crédito con monto: {}", creditoDto.getMonto_principal());
            List<CuotaDto> cronograma = calculoFinancieroService.generarCronogramaFrances(creditoDto);
            return ResponseEntity.ok(cronograma);
        } catch (Exception e) {
            log.error("Error al generar cronograma: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/calcular-indicadores")
    public ResponseEntity<IndicadorFinancieroDto> calcularIndicadores(
            @RequestBody CreditoDto creditoDto) {
        try {
            log.info("Calculando indicadores para crédito");

            // Primero generamos el cronograma
            List<CuotaDto> cronograma = calculoFinancieroService.generarCronogramaFrances(creditoDto);

            // Luego calculamos los indicadores
            IndicadorFinancieroDto indicadores =
                    calculoFinancieroService.calcularIndicadoresFinancieros(creditoDto, cronograma);

            return ResponseEntity.ok(indicadores);
        } catch (Exception e) {
            log.error("Error al calcular indicadores: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/simulacion-completa")
    public ResponseEntity<SimulacionCompletaResponse> simulacionCompleta(
            @RequestBody CreditoDto creditoDto) {
        try {
            log.info("Iniciando simulación completa");

            // Validar datos básicos
            if (creditoDto.getMonto_principal() == null || creditoDto.getMonto_principal() <= 0) {
                throw new RuntimeException("Monto principal inválido");
            }

            if (creditoDto.getTasa_anual() == null || creditoDto.getTasa_anual() <= 0) {
                throw new RuntimeException("Tasa anual inválida");
            }

            if (creditoDto.getPlazo_meses() == null || creditoDto.getPlazo_meses() <= 0) {
                throw new RuntimeException("Plazo inválido");
            }

            // Generar cronograma
            List<CuotaDto> cronograma = calculoFinancieroService.generarCronogramaFrances(creditoDto);

            // Aplicar periodo de gracia si corresponde
            if (creditoDto.getMeses_gracia() != null && creditoDto.getMeses_gracia() > 0 &&
                    creditoDto.getGraciadto() != null) {
                cronograma = calculoFinancieroService.aplicarPeriodoGracia(
                        cronograma,
                        creditoDto.getMeses_gracia(),
                        creditoDto.getGraciadto().getTipo()
                );
            }

            // Calcular indicadores
            IndicadorFinancieroDto indicadores =
                    calculoFinancieroService.calcularIndicadoresFinancieros(creditoDto, cronograma);

            // Calcular resumen
            SimulacionResumenDto resumen = calcularResumen(cronograma, indicadores);

            // Crear respuesta completa
            SimulacionCompletaResponse respuesta = new SimulacionCompletaResponse();
            respuesta.setCredito(creditoDto);
            respuesta.setCronograma(cronograma);
            respuesta.setIndicadores(indicadores);
            respuesta.setResumen(resumen);
            respuesta.setMensaje("Simulación generada exitosamente");
            respuesta.setExito(true);

            log.info("Simulación completada exitosamente");
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            log.error("Error en simulación completa: {}", e.getMessage(), e);

            SimulacionCompletaResponse errorResponse = new SimulacionCompletaResponse();
            errorResponse.setMensaje("Error: " + e.getMessage());
            errorResponse.setExito(false);

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/guardar-simulacion")
    public ResponseEntity<SimulacionGuardadaResponse> guardarSimulacion(
            @RequestBody CreditoDto creditoDto) {
        try {
            log.info("Guardando simulación");

            // Guardar el crédito
            CreditoDto creditoGuardado = creditoService.grabarCredito(creditoDto);

            // Generar cronograma
            List<CuotaDto> cronograma = calculoFinancieroService.generarCronogramaFrances(creditoDto);

            SimulacionGuardadaResponse respuesta = new SimulacionGuardadaResponse();
            respuesta.setCredito(creditoGuardado);
            respuesta.setCronograma(cronograma);
            respuesta.setMensaje("Simulación guardada exitosamente. Crédito ID: " + creditoGuardado.getId_credito());
            respuesta.setExito(true);

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            log.error("Error al guardar simulación: {}", e.getMessage(), e);

            SimulacionGuardadaResponse errorResponse = new SimulacionGuardadaResponse();
            errorResponse.setMensaje("Error: " + e.getMessage());
            errorResponse.setExito(false);

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // Método auxiliar para calcular resumen
    private SimulacionResumenDto calcularResumen(List<CuotaDto> cronograma, IndicadorFinancieroDto indicadores) {
        SimulacionResumenDto resumen = new SimulacionResumenDto();

        if (cronograma != null && !cronograma.isEmpty()) {
            // Total a pagar
            Double totalPagar = cronograma.stream()
                    .mapToDouble(CuotaDto::getTotal_cuota)
                    .sum();
            resumen.setTotalPagar(totalPagar);

            // Total intereses
            Double totalIntereses = cronograma.stream()
                    .mapToDouble(CuotaDto::getInteres_programado)
                    .sum();
            resumen.setTotalIntereses(totalIntereses);

            // Total seguros y cargos
            Double totalCargos = cronograma.stream()
                    .mapToDouble(CuotaDto::getOtros_cargos)
                    .sum();
            resumen.setTotalCargos(totalCargos);

            // Cuota promedio
            resumen.setCuotaPromedio(totalPagar / cronograma.size());
        }

        if (indicadores != null) {
            resumen.setVan(indicadores.getVAN());
            resumen.setTir(indicadores.getTIR());
            resumen.setTcea(indicadores.getTCEA());
            resumen.setTrea(indicadores.getTREA());
        }

        return resumen;
    }

    // DTOs internos para respuestas específicas
    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimulacionCompletaResponse {
        private CreditoDto credito;
        private List<CuotaDto> cronograma;
        private IndicadorFinancieroDto indicadores;
        private SimulacionResumenDto resumen;
        private String mensaje;
        private Boolean exito;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimulacionGuardadaResponse {
        private CreditoDto credito;
        private List<CuotaDto> cronograma;
        private String mensaje;
        private Boolean exito;
    }

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimulacionResumenDto {
        private Double totalPagar;
        private Double totalIntereses;
        private Double totalCargos;
        private Double cuotaPromedio;
        private Double van;
        private Double tir;
        private Double tcea;
        private Double trea;
    }
}
