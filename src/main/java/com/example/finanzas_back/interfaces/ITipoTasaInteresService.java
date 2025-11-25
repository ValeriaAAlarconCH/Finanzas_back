package com.example.finanzas_back.interfaces;

import com.example.finanzas_back.dtos.TipoTasaInteresDto;

import java.util.List;

public interface ITipoTasaInteresService {
    public TipoTasaInteresDto grabarTipoTasaInteres(TipoTasaInteresDto tasadto);
    public List<TipoTasaInteresDto> getTiposTasaInteres();
    void eliminar(Long id);
    public TipoTasaInteresDto actualizar(TipoTasaInteresDto tasadto);
    public TipoTasaInteresDto obtenerPorId(Long id);
}
