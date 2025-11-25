package com.example.finanzas_back.interfaces;

import com.example.finanzas_back.dtos.EntidadFinancieraDto;

import java.util.List;

public interface IEntidadFinancieraService {
    public EntidadFinancieraDto grabarEntidadFinanciera(EntidadFinancieraDto entidaddto);
    public List<EntidadFinancieraDto> getEntidadesFinancieras();
    void eliminar(Long id);
    public EntidadFinancieraDto actualizar(EntidadFinancieraDto entidaddto);
    public EntidadFinancieraDto obtenerPorId(Long id);
}
