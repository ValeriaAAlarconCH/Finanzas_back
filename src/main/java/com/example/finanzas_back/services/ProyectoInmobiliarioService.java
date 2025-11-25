package com.example.finanzas_back.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.finanzas_back.dtos.ProyectoInmobiliarioDto;
import com.example.finanzas_back.entities.ProyectoInmobiliario;
import com.example.finanzas_back.interfaces.IProyectoInmobiliarioService;
import com.example.finanzas_back.repositories.ProyectoInmobiliarioRepository;

import java.util.List;

@Service
public class ProyectoInmobiliarioService implements IProyectoInmobiliarioService {
    @Autowired
    private ProyectoInmobiliarioRepository proyectorepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ProyectoInmobiliarioDto grabarProyectoInmobiliario(ProyectoInmobiliarioDto proyectodto) {
        ProyectoInmobiliario proyecto = modelMapper.map(proyectodto, ProyectoInmobiliario.class);
        ProyectoInmobiliario guardado = proyectorepository.save(proyecto);
        return modelMapper.map(guardado, ProyectoInmobiliarioDto.class);
    }

    @Override
    public List<ProyectoInmobiliarioDto> getProyectosInmobiliarios() {
        return proyectorepository.findAll().stream()
                .map(proyecto -> modelMapper.map(proyecto, ProyectoInmobiliarioDto.class))
                .toList();
    }

    @Override
    public void eliminar(Long id) {
        if (proyectorepository.existsById(id)) {
            proyectorepository.deleteById(id);
        } else {
            throw new RuntimeException("No se encontró el Proyecto Inmobiliario con ID: " + id);
        }
    }

    @Override
    public ProyectoInmobiliarioDto actualizar(ProyectoInmobiliarioDto proyectodto) {
        Long id = proyectodto.getId_proyecto();
        if (id == null) {
            throw new RuntimeException("El ID del proyecto inmobiliario no puede ser nulo");
        }

        ProyectoInmobiliario proyectoExistente = proyectorepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el proyecto inmobiliario con ID: " + id));

        proyectoExistente.setNombre_proyecto(proyectodto.getNombre_proyecto());
        proyectoExistente.setDireccion(proyectodto.getDireccion());
        proyectoExistente.setDescripcion(proyectodto.getDescripcion());
        proyectoExistente.setDesarrollador(proyectodto.getDesarrollador());
        proyectoExistente.setFecha_inicio(proyectodto.getFecha_inicio());
        proyectoExistente.setFecha_entrega_estimada(proyectodto.getFecha_entrega_estimada());

        ProyectoInmobiliario actualizado = proyectorepository.save(proyectoExistente);
        return modelMapper.map(actualizado, ProyectoInmobiliarioDto.class);
    }

    @Override
    public ProyectoInmobiliarioDto obtenerPorId(Long id) {
        ProyectoInmobiliario proyecto = proyectorepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto Inmobiliario no encontrado con ID: " + id));
        return modelMapper.map(proyecto, ProyectoInmobiliarioDto.class);
    }
}
