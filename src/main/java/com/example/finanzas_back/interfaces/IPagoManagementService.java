package com.example.finanzas_back.interfaces;

import com.example.finanzas_back.dtos.*;

import java.time.LocalDate;
import java.util.List;

public interface IPagoManagementService {

    // Registrar un pago y aplicarlo a cuotas
    ResultadoPagoDto registrarPago(PagoDto pagoDto, List<Long> idsCuotas);

    // Aplicar pago a cuotas específicas
    List<PagoCuotaDto> aplicarPagoACuotas(Long idPago, List<Long> idsCuotas, Double montoTotal);

    // Calcular mora por cuota vencida
    Double calcularMora(CuotaDto cuota, LocalDate fechaPago);

    // Calcular interés compensatorio
    Double calcularInteresCompensatorio(CuotaDto cuota, LocalDate fechaPago);

    // Recalcular cronograma después de un pago adelantado
    List<CuotaDto> recalcularCronogramaDespuesPago(Long idCredito, Double montoAdelantado);

    // Generar estado de cuenta
    EstadoCuentaDto generarEstadoCuenta(Long idCredito, LocalDate fechaCorte);

    // Verificar capacidad de pago del cliente
    Boolean verificarCapacidadPago(Long idCliente, Double montoCuotaPropuesta);

    // Calcular cuota máxima según ingreso
    Double calcularCuotaMaxima(Double ingresoMensual, Double porcentajeEndeudamiento);
}
