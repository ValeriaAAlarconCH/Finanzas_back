package com.example.finanzas_back.interfaces;

import com.example.finanzas_back.dtos.UnidadInmobiliariaDto;

import java.util.List;

public interface IUnidadInmobiliariaService {
    public UnidadInmobiliariaDto grabarUnidadInmobiliaria(UnidadInmobiliariaDto unidaddto);
    public List<UnidadInmobiliariaDto> getUnidadesInmobiliarias();
    void eliminar(Long id);
    public UnidadInmobiliariaDto actualizar(UnidadInmobiliariaDto unidaddto);
    public UnidadInmobiliariaDto obtenerPorId(Long id);
}
