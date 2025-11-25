package com.example.finanzas_back.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.finanzas_back.dtos.*;
import com.example.finanzas_back.entities.*;
import com.example.finanzas_back.interfaces.IPagoService;
import com.example.finanzas_back.repositories.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class PagoService implements IPagoService {
    @Autowired
    private PagoRepository pagorepository;

    @Autowired
    private CreditoRepository creditorepository;

    @Autowired
    private MonedaRepository monedarepository;

    @Autowired
    private ClienteRepository clienterepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PagoDto grabarPago(PagoDto dto) {
        Pago pago = modelMapper.map(dto, Pago.class);

        if (dto.getCreditodto() != null && dto.getCreditodto().getId_credito() != null) {
            Credito credito = creditorepository.findById(dto.getCreditodto().getId_credito())
                    .orElseThrow(() -> new RuntimeException("Crédito no encontrado con ID: " + dto.getCreditodto().getId_credito()));
            pago.setCredito(credito);
        } else {
            throw new RuntimeException("Debe proporcionar el ID del crédito");
        }

        if (dto.getMonedadto() != null && dto.getMonedadto().getId_moneda() != null) {
            Moneda moneda = monedarepository.findById(dto.getMonedadto().getId_moneda())
                    .orElseThrow(() -> new RuntimeException("Moneda no encontrada con ID: " + dto.getMonedadto().getId_moneda()));
            pago.setMoneda(moneda);
        } else {
            throw new RuntimeException("Debe proporcionar el ID de la moneda");
        }

        if (dto.getClientedto() != null && dto.getClientedto().getId_cliente() != null) {
            Cliente cliente = clienterepository.findById(dto.getClientedto().getId_cliente())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + dto.getClientedto().getId_cliente()));
            pago.setCliente(cliente);
        } else {
            throw new RuntimeException("Debe proporcionar el ID del cliente");
        }

        Pago guardado = pagorepository.save(pago);
        return modelMapper.map(guardado, PagoDto.class);
    }

    @Override
    public List<PagoDto> getPagos() {
        List<Pago> lista = pagorepository.findAll();
        List<PagoDto> dtoLista = new ArrayList<>();

        for (Pago p : lista) {
            PagoDto dto = modelMapper.map(p, PagoDto.class);

            if (p.getCredito() != null) {
                dto.setCreditodto(modelMapper.map(p.getCredito(), CreditoDto.class));
            }
            if (p.getMoneda() != null) {
                dto.setMonedadto(modelMapper.map(p.getMoneda(), MonedaDto.class));
            }
            if (p.getCliente() != null) {
                dto.setClientedto(modelMapper.map(p.getCliente(), ClienteDto.class));
            }

            dtoLista.add(dto);
        }

        return dtoLista;
    }

    @Override
    public void eliminar(Long id) {
        if (pagorepository.existsById(id)) {
            pagorepository.deleteById(id);
        } else {
            throw new RuntimeException("No se encontró el Pago con ID: " + id);
        }
    }

    @Override
    public PagoDto actualizar(PagoDto pagodto) {
        Long id = pagodto.getId_pago();
        if (id == null) {
            throw new RuntimeException("El ID del pago no puede ser nulo");
        }

        Pago pagoExistente = pagorepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el pago con ID: " + id));

        pagoExistente.setFecha_pago(pagodto.getFecha_pago());
        pagoExistente.setMonto(pagodto.getMonto());
        pagoExistente.setMetodo_pago(pagodto.getMetodo_pago());
        pagoExistente.setReferencia(pagodto.getReferencia());

        if (pagodto.getCreditodto() != null && pagodto.getCreditodto().getId_credito() != null) {
            Credito credito = creditorepository.findById(pagodto.getCreditodto().getId_credito())
                    .orElseThrow(() -> new RuntimeException("Crédito no encontrado"));
            pagoExistente.setCredito(credito);
        }

        if (pagodto.getMonedadto() != null && pagodto.getMonedadto().getId_moneda() != null) {
            Moneda moneda = monedarepository.findById(pagodto.getMonedadto().getId_moneda())
                    .orElseThrow(() -> new RuntimeException("Moneda no encontrada"));
            pagoExistente.setMoneda(moneda);
        }

        if (pagodto.getClientedto() != null && pagodto.getClientedto().getId_cliente() != null) {
            Cliente cliente = clienterepository.findById(pagodto.getClientedto().getId_cliente())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            pagoExistente.setCliente(cliente);
        }

        Pago actualizado = pagorepository.save(pagoExistente);
        return modelMapper.map(actualizado, PagoDto.class);
    }

    @Override
    public PagoDto obtenerPorId(Long id) {
        Pago pago = pagorepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado con ID: " + id));

        PagoDto dto = modelMapper.map(pago, PagoDto.class);

        if (pago.getCredito() != null) {
            dto.setCreditodto(modelMapper.map(pago.getCredito(), CreditoDto.class));
        }
        if (pago.getMoneda() != null) {
            dto.setMonedadto(modelMapper.map(pago.getMoneda(), MonedaDto.class));
        }
        if (pago.getCliente() != null) {
            dto.setClientedto(modelMapper.map(pago.getCliente(), ClienteDto.class));
        }

        return dto;
    }
}
