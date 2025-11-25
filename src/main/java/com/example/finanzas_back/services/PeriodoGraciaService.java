package com.example.finanzas_back.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.finanzas_back.dtos.PeriodoGraciaDto;
import com.example.finanzas_back.entities.PeriodoGracia;
import com.example.finanzas_back.interfaces.IPeriodoGraciaService;
import com.example.finanzas_back.repositories.PeriodoGraciaRepository;

import java.util.List;

@Service
public class PeriodoGraciaService implements IPeriodoGraciaService {
    @Autowired
    private PeriodoGraciaRepository graciarepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PeriodoGraciaDto grabarPeriodoGracia(PeriodoGraciaDto graciadto) {
        PeriodoGracia gracia = modelMapper.map(graciadto, PeriodoGracia.class);
        PeriodoGracia guardado = graciarepository.save(gracia);
        return modelMapper.map(guardado, PeriodoGraciaDto.class);
    }

    @Override
    public List<PeriodoGraciaDto> getPeriodosGracia() {
        return graciarepository.findAll().stream()
                .map(gracia -> modelMapper.map(gracia, PeriodoGraciaDto.class))
                .toList();
    }

    @Override
    public void eliminar(Long id) {
        if (graciarepository.existsById(id)) {
            graciarepository.deleteById(id);
        } else {
            throw new RuntimeException("No se encontró el Periodo de Gracia con ID: " + id);
        }
    }

    @Override
    public PeriodoGraciaDto actualizar(PeriodoGraciaDto graciadto) {
        Long id = graciadto.getId_gracia();
        if (id == null) {
            throw new RuntimeException("El ID del periodo de gracia no puede ser nulo");
        }

        PeriodoGracia graciaExistente = graciarepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el periodo de gracia con ID: " + id));

        graciaExistente.setTipo(graciadto.getTipo());

        PeriodoGracia actualizado = graciarepository.save(graciaExistente);
        return modelMapper.map(actualizado, PeriodoGraciaDto.class);
    }

    @Override
    public PeriodoGraciaDto obtenerPorId(Long id) {
        PeriodoGracia gracia = graciarepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Periodo de Gracia no encontrado con ID: " + id));
        return modelMapper.map(gracia, PeriodoGraciaDto.class);
    }
}
