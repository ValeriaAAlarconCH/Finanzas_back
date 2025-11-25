package com.example.finanzas_back.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.finanzas_back.dtos.IndicadorFinancieroDto;
import com.example.finanzas_back.dtos.CreditoDto;
import com.example.finanzas_back.entities.IndicadorFinanciero;
import com.example.finanzas_back.entities.Credito;
import com.example.finanzas_back.interfaces.IIndicadorFinancieroService;
import com.example.finanzas_back.repositories.IndicadorFinancieroRepository;
import com.example.finanzas_back.repositories.CreditoRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class IndicadorFinancieroService implements IIndicadorFinancieroService {
    @Autowired
    private IndicadorFinancieroRepository indicadorrepository;

    @Autowired
    private CreditoRepository creditorepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public IndicadorFinancieroDto grabarIndicadorFinanciero(IndicadorFinancieroDto dto) {
        IndicadorFinanciero indicador = modelMapper.map(dto, IndicadorFinanciero.class);

        if (dto.getCreditodto() != null && dto.getCreditodto().getId_credito() != null) {
            Credito credito = creditorepository.findById(dto.getCreditodto().getId_credito())
                    .orElseThrow(() -> new RuntimeException("Crédito no encontrado con ID: " + dto.getCreditodto().getId_credito()));
            indicador.setCredito(credito);
        } else {
            throw new RuntimeException("Debe proporcionar el ID del crédito");
        }

        IndicadorFinanciero guardado = indicadorrepository.save(indicador);
        return modelMapper.map(guardado, IndicadorFinancieroDto.class);
    }

    @Override
    public List<IndicadorFinancieroDto> getIndicadoresFinancieros() {
        List<IndicadorFinanciero> lista = indicadorrepository.findAll();
        List<IndicadorFinancieroDto> dtoLista = new ArrayList<>();

        for (IndicadorFinanciero i : lista) {
            IndicadorFinancieroDto dto = modelMapper.map(i, IndicadorFinancieroDto.class);

            if (i.getCredito() != null) {
                dto.setCreditodto(modelMapper.map(i.getCredito(), CreditoDto.class));
            }

            dtoLista.add(dto);
        }

        return dtoLista;
    }

    @Override
    public void eliminar(Long id) {
        if (indicadorrepository.existsById(id)) {
            indicadorrepository.deleteById(id);
        } else {
            throw new RuntimeException("No se encontró el Indicador Financiero con ID: " + id);
        }
    }

    @Override
    public IndicadorFinancieroDto actualizar(IndicadorFinancieroDto indicadordto) {
        Long id = indicadordto.getId_indicador();
        if (id == null) {
            throw new RuntimeException("El ID del indicador financiero no puede ser nulo");
        }

        IndicadorFinanciero indicadorExistente = indicadorrepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el indicador financiero con ID: " + id));

        indicadorExistente.setFecha_calculo(indicadordto.getFecha_calculo());
        indicadorExistente.setVAN(indicadordto.getVAN());
        indicadorExistente.setTIR(indicadordto.getTIR());
        indicadorExistente.setTCEA(indicadordto.getTCEA());
        indicadorExistente.setTREA(indicadordto.getTREA());
        indicadorExistente.setDuracion(indicadordto.getDuracion());
        indicadorExistente.setDuracion_modificada(indicadordto.getDuracion_modificada());
        indicadorExistente.setConvexidad(indicadordto.getConvexidad());

        if (indicadordto.getCreditodto() != null && indicadordto.getCreditodto().getId_credito() != null) {
            Credito credito = creditorepository.findById(indicadordto.getCreditodto().getId_credito())
                    .orElseThrow(() -> new RuntimeException("Crédito no encontrado"));
            indicadorExistente.setCredito(credito);
        } else {
            throw new RuntimeException("Debe proporcionar el ID del crédito");
        }

        IndicadorFinanciero actualizado = indicadorrepository.save(indicadorExistente);
        return modelMapper.map(actualizado, IndicadorFinancieroDto.class);
    }

    @Override
    public IndicadorFinancieroDto obtenerPorId(Long id) {
        IndicadorFinanciero indicador = indicadorrepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Indicador Financiero no encontrado con ID: " + id));

        IndicadorFinancieroDto dto = modelMapper.map(indicador, IndicadorFinancieroDto.class);

        if (indicador.getCredito() != null) {
            dto.setCreditodto(modelMapper.map(indicador.getCredito(), CreditoDto.class));
        }

        return dto;
    }
}
