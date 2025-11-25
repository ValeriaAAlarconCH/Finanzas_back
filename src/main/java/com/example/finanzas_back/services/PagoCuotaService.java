package com.example.finanzas_back.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.finanzas_back.dtos.PagoCuotaDto;
import com.example.finanzas_back.entities.PagoCuota;
import com.example.finanzas_back.entities.PagoCuota.PagoCuotaId;
import com.example.finanzas_back.interfaces.IPagoCuotaService;
import com.example.finanzas_back.repositories.PagoCuotaRepository;

import java.util.List;

@Service
public class PagoCuotaService implements IPagoCuotaService {
    @Autowired
    private PagoCuotaRepository pagocuotarepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PagoCuotaDto grabarPagoCuota(PagoCuotaDto pagocuotadto) {
        PagoCuota pagoCuota = modelMapper.map(pagocuotadto, PagoCuota.class);

        PagoCuotaId id = new PagoCuotaId();
        id.setIdPago(pagocuotadto.getIdPago());
        id.setIdCuota(pagocuotadto.getIdCuota());
        pagoCuota.setIdPago(pagocuotadto.getIdPago());
        pagoCuota.setIdCuota(pagocuotadto.getIdCuota());

        PagoCuota guardado = pagocuotarepository.save(pagoCuota);
        return modelMapper.map(guardado, PagoCuotaDto.class);
    }

    @Override
    public List<PagoCuotaDto> getPagosCuotas() {
        return pagocuotarepository.findAll().stream()
                .map(pagoCuota -> modelMapper.map(pagoCuota, PagoCuotaDto.class))
                .toList();
    }

    @Override
    public void eliminar(Long idPago, Long idCuota) {
        PagoCuotaId id = new PagoCuotaId();
        id.setIdPago(idPago);
        id.setIdCuota(idCuota);

        if (pagocuotarepository.existsById(id)) {
            pagocuotarepository.deleteById(id);
        } else {
            throw new RuntimeException("No se encontró el PagoCuota con ID Pago: " + idPago + " y ID Cuota: " + idCuota);
        }
    }

    @Override
    public PagoCuotaDto actualizar(PagoCuotaDto pagocuotadto) {
        PagoCuotaId id = new PagoCuotaId();
        id.setIdPago(pagocuotadto.getIdPago());
        id.setIdCuota(pagocuotadto.getIdCuota());

        PagoCuota pagoCuotaExistente = pagocuotarepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el PagoCuota con ID Pago: " + pagocuotadto.getIdPago() + " y ID Cuota: " + pagocuotadto.getIdCuota()));

        pagoCuotaExistente.setMonto_aplicado(pagocuotadto.getMonto_aplicado());
        pagoCuotaExistente.setFecha_pago(pagocuotadto.getFecha_pago());

        PagoCuota actualizado = pagocuotarepository.save(pagoCuotaExistente);
        return modelMapper.map(actualizado, PagoCuotaDto.class);
    }

    @Override
    public PagoCuotaDto obtenerPorId(Long idPago, Long idCuota) {
        PagoCuotaId id = new PagoCuotaId();
        id.setIdPago(idPago);
        id.setIdCuota(idCuota);

        PagoCuota pagoCuota = pagocuotarepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PagoCuota no encontrado con ID Pago: " + idPago + " y ID Cuota: " + idCuota));
        return modelMapper.map(pagoCuota, PagoCuotaDto.class);
    }
}
