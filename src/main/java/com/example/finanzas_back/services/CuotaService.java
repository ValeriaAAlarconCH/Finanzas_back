package com.example.finanzas_back.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.finanzas_back.dtos.CuotaDto;
import com.example.finanzas_back.dtos.CreditoDto;
import com.example.finanzas_back.entities.Cuota;
import com.example.finanzas_back.entities.Credito;
import com.example.finanzas_back.interfaces.ICuotaService;
import com.example.finanzas_back.repositories.CuotaRepository;
import com.example.finanzas_back.repositories.CreditoRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class CuotaService implements ICuotaService {
    @Autowired
    private CuotaRepository cuotarepository;

    @Autowired
    private CreditoRepository creditorepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CuotaDto grabarCuota(CuotaDto dto) {
        Cuota cuota = modelMapper.map(dto, Cuota.class);

        if (dto.getCreditodto() != null && dto.getCreditodto().getId_credito() != null) {
            Credito credito = creditorepository.findById(dto.getCreditodto().getId_credito())
                    .orElseThrow(() -> new RuntimeException("Crédito no encontrado con ID: " + dto.getCreditodto().getId_credito()));
            cuota.setCredito(credito);
        } else {
            throw new RuntimeException("Debe proporcionar el ID del crédito");
        }

        Cuota guardado = cuotarepository.save(cuota);
        return modelMapper.map(guardado, CuotaDto.class);
    }

    @Override
    public List<CuotaDto> getCuotas() {
        List<Cuota> lista = cuotarepository.findAll();
        List<CuotaDto> dtoLista = new ArrayList<>();

        for (Cuota c : lista) {
            CuotaDto dto = modelMapper.map(c, CuotaDto.class);

            if (c.getCredito() != null) {
                dto.setCreditodto(modelMapper.map(c.getCredito(), CreditoDto.class));
            }

            dtoLista.add(dto);
        }

        return dtoLista;
    }

    @Override
    public void eliminar(Long id) {
        if (cuotarepository.existsById(id)) {
            cuotarepository.deleteById(id);
        } else {
            throw new RuntimeException("No se encontró la Cuota con ID: " + id);
        }
    }

    @Override
    public CuotaDto actualizar(CuotaDto cuotadto) {
        Long id = cuotadto.getId_cuota();
        if (id == null) {
            throw new RuntimeException("El ID de la cuota no puede ser nulo");
        }

        Cuota cuotaExistente = cuotarepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró la cuota con ID: " + id));

        cuotaExistente.setNumero_cuota(cuotadto.getNumero_cuota());
        cuotaExistente.setFecha_vencimiento(cuotadto.getFecha_vencimiento());
        cuotaExistente.setDias_periodo(cuotadto.getDias_periodo());
        cuotaExistente.setSaldo_inicial(cuotadto.getSaldo_inicial());
        cuotaExistente.setCapital_programado(cuotadto.getCapital_programado());
        cuotaExistente.setInteres_programado(cuotadto.getInteres_programado());
        cuotaExistente.setOtros_cargos(cuotadto.getOtros_cargos());
        cuotaExistente.setTotal_cuota(cuotadto.getTotal_cuota());
        cuotaExistente.setSaldo_final(cuotadto.getSaldo_final());
        cuotaExistente.setEstado(cuotadto.getEstado());

        if (cuotadto.getCreditodto() != null && cuotadto.getCreditodto().getId_credito() != null) {
            Credito credito = creditorepository.findById(cuotadto.getCreditodto().getId_credito())
                    .orElseThrow(() -> new RuntimeException("Crédito no encontrado"));
            cuotaExistente.setCredito(credito);
        } else {
            throw new RuntimeException("Debe proporcionar el ID del crédito");
        }

        Cuota actualizado = cuotarepository.save(cuotaExistente);
        return modelMapper.map(actualizado, CuotaDto.class);
    }

    @Override
    public CuotaDto obtenerPorId(Long id) {
        Cuota cuota = cuotarepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cuota no encontrada con ID: " + id));

        CuotaDto dto = modelMapper.map(cuota, CuotaDto.class);

        if (cuota.getCredito() != null) {
            dto.setCreditodto(modelMapper.map(cuota.getCredito(), CreditoDto.class));
        }

        return dto;
    }
}
