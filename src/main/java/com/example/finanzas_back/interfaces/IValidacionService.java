package com.example.finanzas_back.interfaces;

import com.example.finanzas_back.dtos.ClienteDto;
import com.example.finanzas_back.dtos.CreditoDto;
import com.example.finanzas_back.dtos.ResultadoValidacionDto;

public interface IValidacionService {
    ResultadoValidacionDto validarCliente(ClienteDto cliente);

    ResultadoValidacionDto validarCapacidadPago(ClienteDto cliente, Double montoCuota);

    ResultadoValidacionDto validarHistorialCrediticio(Long idCliente);

    ResultadoValidacionDto validarCredito(CreditoDto credito);

    ResultadoValidacionDto validarMontoCredito(CreditoDto credito, ClienteDto cliente);

    ResultadoValidacionDto validarTasas(CreditoDto credito);

    ResultadoValidacionDto validarPlazo(CreditoDto credito);

    ResultadoValidacionDto validarUnidadInmobiliaria(Long idUnidad);

    ResultadoValidacionDto validarDocumentosCliente(Long idCliente);

    ResultadoValidacionDto validarCompletitudSolicitud(CreditoDto credito);
}
