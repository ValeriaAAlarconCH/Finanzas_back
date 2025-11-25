package com.example.finanzas_back.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.finanzas_back.dtos.EntidadFinancieraDto;
import com.example.finanzas_back.entities.EntidadFinanciera;
import com.example.finanzas_back.interfaces.IEntidadFinancieraService;
import com.example.finanzas_back.repositories.EntidadFinancieraRepository;

import java.util.List;

@Service
public class EntidadFinancieraService implements IEntidadFinancieraService {
    @Autowired
    private EntidadFinancieraRepository entidadrepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public EntidadFinancieraDto grabarEntidadFinanciera(EntidadFinancieraDto entidaddto) {
        EntidadFinanciera entidad = modelMapper.map(entidaddto, EntidadFinanciera.class);
        EntidadFinanciera guardado = entidadrepository.save(entidad);
        return modelMapper.map(guardado, EntidadFinancieraDto.class);
    }

    @Override
    public List<EntidadFinancieraDto> getEntidadesFinancieras() {
        return entidadrepository.findAll().stream()
                .map(entidad -> modelMapper.map(entidad, EntidadFinancieraDto.class))
                .toList();
    }

    @Override
    public void eliminar(Long id) {
        if (entidadrepository.existsById(id)) {
            entidadrepository.deleteById(id);
        } else {
            throw new RuntimeException("No se encontró la Entidad Financiera con ID: " + id);
        }
    }

    @Override
    public EntidadFinancieraDto actualizar(EntidadFinancieraDto entidaddto) {
        Long id = entidaddto.getId_entidad();
        if (id == null) {
            throw new RuntimeException("El ID de la entidad financiera no puede ser nulo");
        }

        EntidadFinanciera entidadExistente = entidadrepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró la entidad financiera con ID: " + id));

        entidadExistente.setNombre(entidaddto.getNombre());
        entidadExistente.setCodigo_autorizacion(entidaddto.getCodigo_autorizacion());
        entidadExistente.setRuc(entidaddto.getRuc());
        entidadExistente.setDireccion(entidaddto.getDireccion());
        entidadExistente.setTelefono(entidaddto.getTelefono());
        entidadExistente.setEmail(entidaddto.getEmail());

        EntidadFinanciera actualizado = entidadrepository.save(entidadExistente);
        return modelMapper.map(actualizado, EntidadFinancieraDto.class);
    }

    @Override
    public EntidadFinancieraDto obtenerPorId(Long id) {
        EntidadFinanciera entidad = entidadrepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entidad Financiera no encontrada con ID: " + id));
        return modelMapper.map(entidad, EntidadFinancieraDto.class);
    }
}
