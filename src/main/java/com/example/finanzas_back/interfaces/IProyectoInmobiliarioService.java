package com.example.finanzas_back.interfaces;

import com.example.finanzas_back.dtos.ProyectoInmobiliarioDto;

import java.util.List;

public interface IProyectoInmobiliarioService {
    public ProyectoInmobiliarioDto grabarProyectoInmobiliario(ProyectoInmobiliarioDto proyectodto);
    public List<ProyectoInmobiliarioDto> getProyectosInmobiliarios();
    void eliminar(Long id);
    public ProyectoInmobiliarioDto actualizar(ProyectoInmobiliarioDto proyectodto);
    public ProyectoInmobiliarioDto obtenerPorId(Long id);
}
