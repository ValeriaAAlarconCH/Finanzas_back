package com.example.finanzas_back.interfaces;

import com.example.finanzas_back.dtos.MonedaDto;

import java.util.List;

public interface IMonedaService {
    public MonedaDto grabarMoneda(MonedaDto monedadto);
    public List<MonedaDto> getMonedas();
    void eliminar(Long id);
    public MonedaDto actualizar(MonedaDto monedadto);
    public MonedaDto obtenerPorId(Long id);
}
