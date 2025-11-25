package com.example.finanzas_back.interfaces;

import com.example.finanzas_back.dtos.PagoDto;

import java.util.List;

public interface IPagoService {
    public PagoDto grabarPago(PagoDto pagodto);
    public List<PagoDto> getPagos();
    void eliminar(Long id);
    public PagoDto actualizar(PagoDto pagodto);
    public PagoDto obtenerPorId(Long id);
}
