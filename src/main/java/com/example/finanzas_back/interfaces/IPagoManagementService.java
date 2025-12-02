package com.example.finanzas_back.interfaces;

import com.example.finanzas_back.dtos.*;

import java.time.LocalDate;
import java.util.List;

public interface IPagoManagementService {
    ResultadoPagoDto registrarPago(PagoDto pagoDto, List<Long> idsCuotas);

    List<PagoCuotaDto> aplicarPagoACuotas(Long idPago, List<Long> idsCuotas, Double montoTotal);

    Double calcularMora(CuotaDto cuota, LocalDate fechaPago);

    Double calcularInteresCompensatorio(CuotaDto cuota, LocalDate fechaPago);

    List<CuotaDto> recalcularCronogramaDespuesPago(Long idCredito, Double montoAdelantado);

    EstadoCuentaDto generarEstadoCuenta(Long idCredito, LocalDate fechaCorte);

    Boolean verificarCapacidadPago(Long idCliente, Double montoCuotaPropuesta);

    Double calcularCuotaMaxima(Double ingresoMensual, Double porcentajeEndeudamiento);
}
