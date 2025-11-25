package com.example.finanzas_back.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.finanzas_back.dtos.*;
import com.example.finanzas_back.entities.*;
import com.example.finanzas_back.interfaces.IConfiguracionService;
import com.example.finanzas_back.repositories.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConfiguracionService implements IConfiguracionService {
    @Autowired
    private ConfiguracionRepository configuracionrepository;

    @Autowired
    private ClienteRepository clienterepository;

    @Autowired
    private MonedaRepository monedarepository;

    @Autowired
    private TipoTasaInteresRepository tasarepository;

    @Autowired
    private CapitalizacionRepository capitalizacionrepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ConfiguracionDto grabarConfiguracion(ConfiguracionDto dto) {
        Configuracion configuracion = modelMapper.map(dto, Configuracion.class);

        if (dto.getClientedto() != null && dto.getClientedto().getId_cliente() != null) {
            Cliente cliente = clienterepository.findById(dto.getClientedto().getId_cliente())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + dto.getClientedto().getId_cliente()));
            configuracion.setCliente(cliente);
        } else {
            throw new RuntimeException("Debe proporcionar el ID del cliente");
        }

        if (dto.getMonedadto() != null && dto.getMonedadto().getId_moneda() != null) {
            Moneda moneda = monedarepository.findById(dto.getMonedadto().getId_moneda())
                    .orElseThrow(() -> new RuntimeException("Moneda no encontrada con ID: " + dto.getMonedadto().getId_moneda()));
            configuracion.setMoneda(moneda);
        } else {
            throw new RuntimeException("Debe proporcionar el ID de la moneda");
        }

        if (dto.getTasadto() != null && dto.getTasadto().getId_tasa() != null) {
            TipoTasaInteres tasa = tasarepository.findById(dto.getTasadto().getId_tasa())
                    .orElseThrow(() -> new RuntimeException("Tasa de interés no encontrada con ID: " + dto.getTasadto().getId_tasa()));
            configuracion.setTasa(tasa);
        } else {
            throw new RuntimeException("Debe proporcionar el ID de la tasa de interés");
        }

        if (dto.getCapitalizaciondto() != null && dto.getCapitalizaciondto().getId_capitalizacion() != null) {
            Capitalizacion capitalizacion = capitalizacionrepository.findById(dto.getCapitalizaciondto().getId_capitalizacion())
                    .orElseThrow(() -> new RuntimeException("Capitalización no encontrada con ID: " + dto.getCapitalizaciondto().getId_capitalizacion()));
            configuracion.setCapitalizacion(capitalizacion);
        } else {
            throw new RuntimeException("Debe proporcionar el ID de la capitalización");
        }

        Configuracion guardado = configuracionrepository.save(configuracion);
        return modelMapper.map(guardado, ConfiguracionDto.class);
    }

    @Override
    public List<ConfiguracionDto> getConfiguraciones() {
        List<Configuracion> lista = configuracionrepository.findAll();
        List<ConfiguracionDto> dtoLista = new ArrayList<>();

        for (Configuracion c : lista) {
            ConfiguracionDto dto = modelMapper.map(c, ConfiguracionDto.class);

            if (c.getCliente() != null) {
                dto.setClientedto(modelMapper.map(c.getCliente(), ClienteDto.class));
            }
            if (c.getMoneda() != null) {
                dto.setMonedadto(modelMapper.map(c.getMoneda(), MonedaDto.class));
            }
            if (c.getTasa() != null) {
                dto.setTasadto(modelMapper.map(c.getTasa(), TipoTasaInteresDto.class));
            }
            if (c.getCapitalizacion() != null) {
                dto.setCapitalizaciondto(modelMapper.map(c.getCapitalizacion(), CapitalizacionDto.class));
            }

            dtoLista.add(dto);
        }

        return dtoLista;
    }

    @Override
    public void eliminar(Long id) {
        if (configuracionrepository.existsById(id)) {
            configuracionrepository.deleteById(id);
        } else {
            throw new RuntimeException("No se encontró la Configuración con ID: " + id);
        }
    }

    @Override
    public ConfiguracionDto actualizar(ConfiguracionDto configuraciondto) {
        Long id = configuraciondto.getId_config();
        if (id == null) {
            throw new RuntimeException("El ID de la configuración no puede ser nulo");
        }

        Configuracion configuracionExistente = configuracionrepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró la configuración con ID: " + id));

        configuracionExistente.setConvencion_dias(configuraciondto.getConvencion_dias());
        configuracionExistente.setPeriodicidad(configuraciondto.getPeriodicidad());

        if (configuraciondto.getClientedto() != null && configuraciondto.getClientedto().getId_cliente() != null) {
            Cliente cliente = clienterepository.findById(configuraciondto.getClientedto().getId_cliente())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            configuracionExistente.setCliente(cliente);
        }

        if (configuraciondto.getMonedadto() != null && configuraciondto.getMonedadto().getId_moneda() != null) {
            Moneda moneda = monedarepository.findById(configuraciondto.getMonedadto().getId_moneda())
                    .orElseThrow(() -> new RuntimeException("Moneda no encontrada"));
            configuracionExistente.setMoneda(moneda);
        }

        if (configuraciondto.getTasadto() != null && configuraciondto.getTasadto().getId_tasa() != null) {
            TipoTasaInteres tasa = tasarepository.findById(configuraciondto.getTasadto().getId_tasa())
                    .orElseThrow(() -> new RuntimeException("Tasa no encontrada"));
            configuracionExistente.setTasa(tasa);
        }

        if (configuraciondto.getCapitalizaciondto() != null && configuraciondto.getCapitalizaciondto().getId_capitalizacion() != null) {
            Capitalizacion capitalizacion = capitalizacionrepository.findById(configuraciondto.getCapitalizaciondto().getId_capitalizacion())
                    .orElseThrow(() -> new RuntimeException("Capitalización no encontrada"));
            configuracionExistente.setCapitalizacion(capitalizacion);
        }

        Configuracion actualizado = configuracionrepository.save(configuracionExistente);
        return modelMapper.map(actualizado, ConfiguracionDto.class);
    }

    @Override
    public ConfiguracionDto obtenerPorId(Long id) {
        Configuracion configuracion = configuracionrepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Configuración no encontrada con ID: " + id));

        ConfiguracionDto dto = modelMapper.map(configuracion, ConfiguracionDto.class);

        if (configuracion.getCliente() != null) {
            dto.setClientedto(modelMapper.map(configuracion.getCliente(), ClienteDto.class));
        }
        if (configuracion.getMoneda() != null) {
            dto.setMonedadto(modelMapper.map(configuracion.getMoneda(), MonedaDto.class));
        }
        if (configuracion.getTasa() != null) {
            dto.setTasadto(modelMapper.map(configuracion.getTasa(), TipoTasaInteresDto.class));
        }
        if (configuracion.getCapitalizacion() != null) {
            dto.setCapitalizaciondto(modelMapper.map(configuracion.getCapitalizacion(), CapitalizacionDto.class));
        }

        return dto;
    }
}
