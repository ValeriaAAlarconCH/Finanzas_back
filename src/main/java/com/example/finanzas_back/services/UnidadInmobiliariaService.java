package com.example.finanzas_back.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.finanzas_back.dtos.*;
import com.example.finanzas_back.entities.*;
import com.example.finanzas_back.interfaces.IUnidadInmobiliariaService;
import com.example.finanzas_back.repositories.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class UnidadInmobiliariaService implements IUnidadInmobiliariaService {
    @Autowired
    private UnidadInmobiliariaRepository unidadrepository;

    @Autowired
    private ProyectoInmobiliarioRepository proyectorepository;

    @Autowired
    private MonedaRepository monedarepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UnidadInmobiliariaDto grabarUnidadInmobiliaria(UnidadInmobiliariaDto dto) {
        UnidadInmobiliaria unidad = modelMapper.map(dto, UnidadInmobiliaria.class);

        if (dto.getProyectodto() != null && dto.getProyectodto().getId_proyecto() != null) {
            ProyectoInmobiliario proyecto = proyectorepository.findById(dto.getProyectodto().getId_proyecto())
                    .orElseThrow(() -> new RuntimeException("Proyecto inmobiliario no encontrado con ID: " + dto.getProyectodto().getId_proyecto()));
            unidad.setProyecto(proyecto);
        } else {
            throw new RuntimeException("Debe proporcionar el ID del proyecto inmobiliario");
        }

        if (dto.getMonedadto() != null && dto.getMonedadto().getId_moneda() != null) {
            Moneda moneda = monedarepository.findById(dto.getMonedadto().getId_moneda())
                    .orElseThrow(() -> new RuntimeException("Moneda no encontrada con ID: " + dto.getMonedadto().getId_moneda()));
            unidad.setMoneda(moneda);
        } else {
            throw new RuntimeException("Debe proporcionar el ID de la moneda");
        }

        UnidadInmobiliaria guardado = unidadrepository.save(unidad);
        return modelMapper.map(guardado, UnidadInmobiliariaDto.class);
    }

    @Override
    public List<UnidadInmobiliariaDto> getUnidadesInmobiliarias() {
        List<UnidadInmobiliaria> lista = unidadrepository.findAll();
        List<UnidadInmobiliariaDto> dtoLista = new ArrayList<>();

        for (UnidadInmobiliaria u : lista) {
            UnidadInmobiliariaDto dto = modelMapper.map(u, UnidadInmobiliariaDto.class);

            if (u.getProyecto() != null) {
                dto.setProyectodto(modelMapper.map(u.getProyecto(), ProyectoInmobiliarioDto.class));
            }
            if (u.getMoneda() != null) {
                dto.setMonedadto(modelMapper.map(u.getMoneda(), MonedaDto.class));
            }

            dtoLista.add(dto);
        }

        return dtoLista;
    }

    @Override
    public void eliminar(Long id) {
        if (unidadrepository.existsById(id)) {
            unidadrepository.deleteById(id);
        } else {
            throw new RuntimeException("No se encontró la Unidad Inmobiliaria con ID: " + id);
        }
    }

    @Override
    public UnidadInmobiliariaDto actualizar(UnidadInmobiliariaDto unidaddto) {
        Long id = unidaddto.getId_unidad();
        if (id == null) {
            throw new RuntimeException("El ID de la unidad inmobiliaria no puede ser nulo");
        }

        UnidadInmobiliaria unidadExistente = unidadrepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró la unidad inmobiliaria con ID: " + id));

        unidadExistente.setCodigo_unidad(unidaddto.getCodigo_unidad());
        unidadExistente.setTipo(unidaddto.getTipo());
        unidadExistente.setArea_m2(unidaddto.getArea_m2());
        unidadExistente.setNum_dormitorios(unidaddto.getNum_dormitorios());
        unidadExistente.setNum_banos(unidaddto.getNum_banos());
        unidadExistente.setPiso(unidaddto.getPiso());
        unidadExistente.setPrecio_lista(unidaddto.getPrecio_lista());
        unidadExistente.setPrecio_venta(unidaddto.getPrecio_venta());
        unidadExistente.setEstado(unidaddto.getEstado());
        unidadExistente.setDescripcion(unidaddto.getDescripcion());

        if (unidaddto.getProyectodto() != null && unidaddto.getProyectodto().getId_proyecto() != null) {
            ProyectoInmobiliario proyecto = proyectorepository.findById(unidaddto.getProyectodto().getId_proyecto())
                    .orElseThrow(() -> new RuntimeException("Proyecto inmobiliario no encontrado"));
            unidadExistente.setProyecto(proyecto);
        }

        if (unidaddto.getMonedadto() != null && unidaddto.getMonedadto().getId_moneda() != null) {
            Moneda moneda = monedarepository.findById(unidaddto.getMonedadto().getId_moneda())
                    .orElseThrow(() -> new RuntimeException("Moneda no encontrada"));
            unidadExistente.setMoneda(moneda);
        }

        UnidadInmobiliaria actualizado = unidadrepository.save(unidadExistente);
        return modelMapper.map(actualizado, UnidadInmobiliariaDto.class);
    }

    @Override
    public UnidadInmobiliariaDto obtenerPorId(Long id) {
        UnidadInmobiliaria unidad = unidadrepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unidad Inmobiliaria no encontrada con ID: " + id));

        UnidadInmobiliariaDto dto = modelMapper.map(unidad, UnidadInmobiliariaDto.class);

        if (unidad.getProyecto() != null) {
            dto.setProyectodto(modelMapper.map(unidad.getProyecto(), ProyectoInmobiliarioDto.class));
        }
        if (unidad.getMoneda() != null) {
            dto.setMonedadto(modelMapper.map(unidad.getMoneda(), MonedaDto.class));
        }

        return dto;
    }
}
