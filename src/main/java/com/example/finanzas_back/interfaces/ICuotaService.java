package com.example.finanzas_back.interfaces;

import com.example.finanzas_back.dtos.CuotaDto;

import java.util.List;

public interface ICuotaService {
    public CuotaDto grabarCuota(CuotaDto cuotadto);
    public List<CuotaDto> getCuotas();
    void eliminar(Long id);
    public CuotaDto actualizar(CuotaDto cuotadto);
    public CuotaDto obtenerPorId(Long id);
}
