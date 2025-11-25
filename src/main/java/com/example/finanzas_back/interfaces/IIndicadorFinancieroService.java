package com.example.finanzas_back.interfaces;

import com.example.finanzas_back.dtos.IndicadorFinancieroDto;

import java.util.List;

public interface IIndicadorFinancieroService {
    public IndicadorFinancieroDto grabarIndicadorFinanciero(IndicadorFinancieroDto indicadordto);
    public List<IndicadorFinancieroDto> getIndicadoresFinancieros();
    void eliminar(Long id);
    public IndicadorFinancieroDto actualizar(IndicadorFinancieroDto indicadordto);
    public IndicadorFinancieroDto obtenerPorId(Long id);
}
