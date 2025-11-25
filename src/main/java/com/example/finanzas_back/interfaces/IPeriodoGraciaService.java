package com.example.finanzas_back.interfaces;

import com.example.finanzas_back.dtos.PeriodoGraciaDto;

import java.util.List;

public interface IPeriodoGraciaService {
    public PeriodoGraciaDto grabarPeriodoGracia(PeriodoGraciaDto graciadto);
    public List<PeriodoGraciaDto> getPeriodosGracia();
    void eliminar(Long id);
    public PeriodoGraciaDto actualizar(PeriodoGraciaDto graciadto);
    public PeriodoGraciaDto obtenerPorId(Long id);
}
