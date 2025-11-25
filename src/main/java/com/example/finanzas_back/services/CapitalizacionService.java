package com.example.finanzas_back.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.finanzas_back.dtos.CapitalizacionDto;
import com.example.finanzas_back.entities.Capitalizacion;
import com.example.finanzas_back.interfaces.ICapitalizacionService;
import com.example.finanzas_back.repositories.CapitalizacionRepository;

import java.util.List;

@Service
public class CapitalizacionService implements ICapitalizacionService {
    @Autowired
    private CapitalizacionRepository capitalizacionrepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CapitalizacionDto grabarCapitalizacion(CapitalizacionDto capitalizaciondto) {
        Capitalizacion capitalizacion = modelMapper.map(capitalizaciondto, Capitalizacion.class);
        Capitalizacion guardado = capitalizacionrepository.save(capitalizacion);
        return modelMapper.map(guardado, CapitalizacionDto.class);
    }

    @Override
    public List<CapitalizacionDto> getCapitalizaciones() {
        return capitalizacionrepository.findAll().stream()
                .map(capitalizacion -> modelMapper.map(capitalizacion, CapitalizacionDto.class))
                .toList();
    }

    @Override
    public void eliminar(Long id) {
        if (capitalizacionrepository.existsById(id)) {
            capitalizacionrepository.deleteById(id);
        } else {
            throw new RuntimeException("No se encontró la Capitalización con ID: " + id);
        }
    }

    @Override
    public CapitalizacionDto actualizar(CapitalizacionDto capitalizaciondto) {
        Long id = capitalizaciondto.getId_capitalizacion();
        if (id == null) {
            throw new RuntimeException("El ID de la capitalización no puede ser nulo");
        }

        Capitalizacion capitalizacionExistente = capitalizacionrepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró la capitalización con ID: " + id));

        capitalizacionExistente.setNombre(capitalizaciondto.getNombre());
        capitalizacionExistente.setPeriodos_por_ano(capitalizaciondto.getPeriodos_por_ano());

        Capitalizacion actualizado = capitalizacionrepository.save(capitalizacionExistente);
        return modelMapper.map(actualizado, CapitalizacionDto.class);
    }

    @Override
    public CapitalizacionDto obtenerPorId(Long id) {
        Capitalizacion capitalizacion = capitalizacionrepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Capitalización no encontrada con ID: " + id));
        return modelMapper.map(capitalizacion, CapitalizacionDto.class);
    }
}
