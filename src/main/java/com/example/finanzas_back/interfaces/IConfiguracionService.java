package com.example.finanzas_back.interfaces;

import com.example.finanzas_back.dtos.ConfiguracionDto;

import java.util.List;

public interface IConfiguracionService {
    public ConfiguracionDto grabarConfiguracion(ConfiguracionDto configuraciondto);
    public List<ConfiguracionDto> getConfiguraciones();
    void eliminar(Long id);
    public ConfiguracionDto actualizar(ConfiguracionDto configuraciondto);
    public ConfiguracionDto obtenerPorId(Long id);
}
