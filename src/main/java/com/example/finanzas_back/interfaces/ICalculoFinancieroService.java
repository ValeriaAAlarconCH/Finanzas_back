package com.example.finanzas_back.interfaces;

import com.example.finanzas_back.dtos.CapitalizacionDto;
import com.example.finanzas_back.dtos.CuotaDto;
import com.example.finanzas_back.dtos.IndicadorFinancieroDto;
import com.example.finanzas_back.dtos.CreditoDto;

import java.util.List;

public interface ICalculoFinancieroService {
    // Metodo francés vencido ordinario (CORREGIDO: solo uno)
    List<CuotaDto> generarCronogramaFrances(CreditoDto creditoDto);

    // Cálculo de tasas periódicas (CORREGIDO: usar DTO)
    Double calcularTasaPeriodica(Double tasaAnual, String tipoTasa, CapitalizacionDto capitalizacionDto);

    // Cálculo de cuota constante (metodo francés)
    Double calcularCuotaConstante(Double monto, Double tasaPeriodica, Integer plazo);

    // Cálculo de indicadores financieros
    IndicadorFinancieroDto calcularIndicadoresFinancieros(CreditoDto creditoDto, List<CuotaDto> cronograma);

    // Cálculo de VAN (Valor Actual Neto)
    Double calcularVAN(List<CuotaDto> flujos, Double tasaDescuento);

    // Cálculo de TIR (Tasa Interna de Retorno)
    Double calcularTIR(List<CuotaDto> flujos);

    // Cálculo de TCEA (Tasa de Costo Efectiva Anual)
    Double calcularTCEA(Double tir, Integer periodosPorAno);

    // Cálculo de TREA (Tasa de Rendimiento Efectiva Anual)
    Double calcularTREA(Double tir, Integer periodosPorAno);

    // Cálculo de duración Macaulay
    Double calcularDuracion(List<CuotaDto> cronograma, Double tasaDescuento);

    // Cálculo de duración modificada
    Double calcularDuracionModificada(Double duracion, Double tasaDescuento);

    // Cálculo de convexidad
    Double calcularConvexidad(List<CuotaDto> cronograma, Double tasaDescuento);

    // Procesar periodos de gracia (CORREGIDO: solo uno)
    List<CuotaDto> aplicarPeriodoGracia(List<CuotaDto> cronograma, Integer mesesGracia, String tipoGracia);

    // Nuevo metodo: Calcular seguro desgravamen
    Double calcularSeguroDesgravamen(Double saldo, Double porcentaje);

    // Nuevo metodo: Calcular seguro inmueble
    Double calcularSeguroInmueble(Double saldo, Double porcentaje);
}