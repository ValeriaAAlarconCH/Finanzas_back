package com.example.finanzas_back.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.finanzas_back.dtos.TipoTasaInteresDto;
import com.example.finanzas_back.entities.TipoTasaInteres;
import com.example.finanzas_back.interfaces.ITipoTasaInteresService;
import com.example.finanzas_back.repositories.TipoTasaInteresRepository;

import java.util.List;

@Service
public class TipoTasaInteresService implements ITipoTasaInteresService {
    @Autowired
    private TipoTasaInteresRepository tasarepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public TipoTasaInteresDto grabarTipoTasaInteres(TipoTasaInteresDto tasadto) {
        TipoTasaInteres tasa = modelMapper.map(tasadto, TipoTasaInteres.class);
        TipoTasaInteres guardado = tasarepository.save(tasa);
        return modelMapper.map(guardado, TipoTasaInteresDto.class);
    }

    @Override
    public List<TipoTasaInteresDto> getTiposTasaInteres() {
        return tasarepository.findAll().stream()
                .map(tasa -> modelMapper.map(tasa, TipoTasaInteresDto.class))
                .toList();
    }

    @Override
    public void eliminar(Long id) {
        if (tasarepository.existsById(id)) {
            tasarepository.deleteById(id);
        } else {
            throw new RuntimeException("No se encontró el Tipo de Tasa de Interés con ID: " + id);
        }
    }

    @Override
    public TipoTasaInteresDto actualizar(TipoTasaInteresDto tasadto) {
        Long id = tasadto.getId_tasa();
        if (id == null) {
            throw new RuntimeException("El ID del tipo de tasa de interés no puede ser nulo");
        }

        TipoTasaInteres tasaExistente = tasarepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el tipo de tasa de interés con ID: " + id));

        tasaExistente.setTipo(tasadto.getTipo());

        TipoTasaInteres actualizado = tasarepository.save(tasaExistente);
        return modelMapper.map(actualizado, TipoTasaInteresDto.class);
    }

    @Override
    public TipoTasaInteresDto obtenerPorId(Long id) {
        TipoTasaInteres tasa = tasarepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tipo de Tasa de Interés no encontrado con ID: " + id));
        return modelMapper.map(tasa, TipoTasaInteresDto.class);
    }
}
