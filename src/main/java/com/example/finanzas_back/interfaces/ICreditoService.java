package com.example.finanzas_back.interfaces;

import com.example.finanzas_back.dtos.CreditoDto;

import java.util.List;

public interface ICreditoService {
    public CreditoDto grabarCredito(CreditoDto creditodto);
    public List<CreditoDto> getCreditos();
    void eliminar(Long id);
    public CreditoDto actualizar(CreditoDto creditodto);
    public CreditoDto obtenerPorId(Long id);
}
