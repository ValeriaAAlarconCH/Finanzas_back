package com.example.finanzas_back.interfaces;

import com.example.finanzas_back.dtos.CapitalizacionDto;
import com.example.finanzas_back.dtos.CuotaDto;
import com.example.finanzas_back.dtos.IndicadorFinancieroDto;
import com.example.finanzas_back.dtos.CreditoDto;

import java.util.List;

public interface ICalculoFinancieroService {
    List<CuotaDto> generarCronogramaFrances(CreditoDto creditoDto);

    Double calcularTasaPeriodica(Double tasaAnual, String tipoTasa, CapitalizacionDto capitalizacionDto);

    Double calcularCuotaConstante(Double monto, Double tasaPeriodica, Integer plazo);

    IndicadorFinancieroDto calcularIndicadoresFinancieros(CreditoDto creditoDto, List<CuotaDto> cronograma);

    Double calcularVAN(List<CuotaDto> flujos, Double tasaDescuento);

    Double calcularTIR(List<CuotaDto> flujos);

    Double calcularTCEA(Double tir, Integer periodosPorAno);

    Double calcularTREA(Double tir, Integer periodosPorAno);

    Double calcularDuracion(List<CuotaDto> cronograma, Double tasaDescuento);

    Double calcularDuracionModificada(Double duracion, Double tasaDescuento);

    Double calcularConvexidad(List<CuotaDto> cronograma, Double tasaDescuento);

    List<CuotaDto> aplicarPeriodoGracia(List<CuotaDto> cronograma, Integer mesesGracia, String tipoGracia);

    Double calcularSeguroDesgravamen(Double saldo, Double porcentaje);

    Double calcularSeguroInmueble(Double saldo, Double porcentaje);
}