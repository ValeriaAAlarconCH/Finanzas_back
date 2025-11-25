package com.example.finanzas_back.interfaces;

import com.example.finanzas_back.dtos.PagoCuotaDto;

import java.util.List;

public interface IPagoCuotaService {
    public PagoCuotaDto grabarPagoCuota(PagoCuotaDto pagocuotadto);
    public List<PagoCuotaDto> getPagosCuotas();
    void eliminar(Long idPago, Long idCuota);
    public PagoCuotaDto actualizar(PagoCuotaDto pagocuotadto);
    public PagoCuotaDto obtenerPorId(Long idPago, Long idCuota);
}
