package com.example.finanzas_back.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.finanzas_back.dtos.MonedaDto;
import com.example.finanzas_back.entities.Moneda;
import com.example.finanzas_back.interfaces.IMonedaService;
import com.example.finanzas_back.repositories.MonedaRepository;

import java.util.List;

@Service
public class MonedaService implements IMonedaService {
    @Autowired
    private MonedaRepository monedarepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public MonedaDto grabarMoneda(MonedaDto monedadto) {
        Moneda moneda = modelMapper.map(monedadto, Moneda.class);
        Moneda guardado = monedarepository.save(moneda);
        return modelMapper.map(guardado, MonedaDto.class);
    }

    @Override
    public List<MonedaDto> getMonedas() {
        return monedarepository.findAll().stream()
                .map(moneda -> modelMapper.map(moneda, MonedaDto.class))
                .toList();
    }

    @Override
    public void eliminar(Long id) {
        if (monedarepository.existsById(id)) {
            monedarepository.deleteById(id);
        } else {
            throw new RuntimeException("No se encontró la Moneda con ID: " + id);
        }
    }

    @Override
    public MonedaDto actualizar(MonedaDto monedadto) {
        Long id = monedadto.getId_moneda();
        if (id == null) {
            throw new RuntimeException("El ID de la moneda no puede ser nulo");
        }

        Moneda monedaExistente = monedarepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró la moneda con ID: " + id));

        monedaExistente.setCodigo(monedadto.getCodigo());
        monedaExistente.setNombre(monedadto.getNombre());
        monedaExistente.setSimbolo(monedadto.getSimbolo());

        Moneda actualizado = monedarepository.save(monedaExistente);
        return modelMapper.map(actualizado, MonedaDto.class);
    }

    @Override
    public MonedaDto obtenerPorId(Long id) {
        Moneda moneda = monedarepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Moneda no encontrada con ID: " + id));
        return modelMapper.map(moneda, MonedaDto.class);
    }
}
