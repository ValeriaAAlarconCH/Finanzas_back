package com.example.finanzas_back.interfaces;

import com.example.finanzas_back.dtos.CapitalizacionDto;

import java.util.List;

public interface ICapitalizacionService {
    public CapitalizacionDto grabarCapitalizacion(CapitalizacionDto capitalizaciondto);
    public List<CapitalizacionDto> getCapitalizaciones();
    void eliminar(Long id);
    public CapitalizacionDto actualizar(CapitalizacionDto capitalizaciondto);
    public CapitalizacionDto obtenerPorId(Long id);
}
