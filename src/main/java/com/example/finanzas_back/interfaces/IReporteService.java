package com.example.finanzas_back.interfaces;

import com.example.finanzas_back.dtos.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IReporteService {
    ReporteCreditosDto generarReporteCreditos(LocalDate fechaInicio, LocalDate fechaFin);

    ReportePagosDto generarReportePagos(LocalDate fechaInicio, LocalDate fechaFin);

    ReporteMorosidadDto generarReporteMorosidad();

    DashboardDto obtenerDashboard();

    EstadisticasClienteDto obtenerEstadisticasCliente(Long idCliente);

    EstadisticasProyectoDto obtenerEstadisticasProyecto(Long idProyecto);

    Map<String, Double> calcularKPIs();

    byte[] exportarReporteCreditosExcel(LocalDate fechaInicio, LocalDate fechaFin);

    byte[] exportarEstadoCuentaPdf(Long idCredito);

    byte[] exportarCronogramaPdf(Long idCredito);
}
