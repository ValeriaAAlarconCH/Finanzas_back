package com.example.finanzas_back.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.finanzas_back.dtos.*;
import com.example.finanzas_back.entities.*;
import com.example.finanzas_back.interfaces.ICreditoService;
import com.example.finanzas_back.repositories.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class CreditoService implements ICreditoService {
    @Autowired
    private CreditoRepository creditorepository;

    @Autowired
    private ClienteRepository clienterepository;

    @Autowired
    private UnidadInmobiliariaRepository unidadrepository;

    @Autowired
    private EntidadFinancieraRepository entidadrepository;

    @Autowired
    private MonedaRepository monedarepository;

    @Autowired
    private TipoTasaInteresRepository tasarepository;

    @Autowired
    private CapitalizacionRepository capitalizacionrepository;

    @Autowired
    private PeriodoGraciaRepository graciarepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CreditoDto grabarCredito(CreditoDto dto) {
        Credito credito = modelMapper.map(dto, Credito.class);

        if (dto.getClientedto() != null && dto.getClientedto().getId_cliente() != null) {
            Cliente cliente = clienterepository.findById(dto.getClientedto().getId_cliente())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + dto.getClientedto().getId_cliente()));
            credito.setCliente(cliente);
        } else {
            throw new RuntimeException("Debe proporcionar el ID del cliente");
        }

        if (dto.getUnidaddto() != null && dto.getUnidaddto().getId_unidad() != null) {
            UnidadInmobiliaria unidad = unidadrepository.findById(dto.getUnidaddto().getId_unidad())
                    .orElseThrow(() -> new RuntimeException("Unidad inmobiliaria no encontrada con ID: " + dto.getUnidaddto().getId_unidad()));
            credito.setUnidad(unidad);
        } else {
            throw new RuntimeException("Debe proporcionar el ID de la unidad inmobiliaria");
        }

        if (dto.getEntidaddto() != null && dto.getEntidaddto().getId_entidad() != null) {
            EntidadFinanciera entidad = entidadrepository.findById(dto.getEntidaddto().getId_entidad())
                    .orElseThrow(() -> new RuntimeException("Entidad financiera no encontrada con ID: " + dto.getEntidaddto().getId_entidad()));
            credito.setEntidad(entidad);
        } else {
            throw new RuntimeException("Debe proporcionar el ID de la entidad financiera");
        }

        if (dto.getMonedadto() != null && dto.getMonedadto().getId_moneda() != null) {
            Moneda moneda = monedarepository.findById(dto.getMonedadto().getId_moneda())
                    .orElseThrow(() -> new RuntimeException("Moneda no encontrada con ID: " + dto.getMonedadto().getId_moneda()));
            credito.setMoneda(moneda);
        } else {
            throw new RuntimeException("Debe proporcionar el ID de la moneda");
        }

        if (dto.getTasadto() != null && dto.getTasadto().getId_tasa() != null) {
            TipoTasaInteres tasa = tasarepository.findById(dto.getTasadto().getId_tasa())
                    .orElseThrow(() -> new RuntimeException("Tasa de interés no encontrada con ID: " + dto.getTasadto().getId_tasa()));
            credito.setTasa(tasa);
        } else {
            throw new RuntimeException("Debe proporcionar el ID de la tasa de interés");
        }

        if (dto.getCapitalizaciondto() != null && dto.getCapitalizaciondto().getId_capitalizacion() != null) {
            Capitalizacion capitalizacion = capitalizacionrepository.findById(dto.getCapitalizaciondto().getId_capitalizacion())
                    .orElseThrow(() -> new RuntimeException("Capitalización no encontrada con ID: " + dto.getCapitalizaciondto().getId_capitalizacion()));
            credito.setCapitalizacion(capitalizacion);
        } else {
            throw new RuntimeException("Debe proporcionar el ID de la capitalización");
        }

        if (dto.getGraciadto() != null && dto.getGraciadto().getId_gracia() != null) {
            PeriodoGracia gracia = graciarepository.findById(dto.getGraciadto().getId_gracia())
                    .orElseThrow(() -> new RuntimeException("Periodo de gracia no encontrado con ID: " + dto.getGraciadto().getId_gracia()));
            credito.setGracia(gracia);
        } else {
            throw new RuntimeException("Debe proporcionar el ID del periodo de gracia");
        }

        Credito guardado = creditorepository.save(credito);
        return modelMapper.map(guardado, CreditoDto.class);
    }

    @Override
    public List<CreditoDto> getCreditos() {
        List<Credito> lista = creditorepository.findAll();
        List<CreditoDto> dtoLista = new ArrayList<>();

        for (Credito c : lista) {
            CreditoDto dto = modelMapper.map(c, CreditoDto.class);

            if (c.getCliente() != null) {
                dto.setClientedto(modelMapper.map(c.getCliente(), ClienteDto.class));
            }
            if (c.getUnidad() != null) {
                dto.setUnidaddto(modelMapper.map(c.getUnidad(), UnidadInmobiliariaDto.class));
            }
            if (c.getEntidad() != null) {
                dto.setEntidaddto(modelMapper.map(c.getEntidad(), EntidadFinancieraDto.class));
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
            if (c.getGracia() != null) {
                dto.setGraciadto(modelMapper.map(c.getGracia(), PeriodoGraciaDto.class));
            }

            dtoLista.add(dto);
        }

        return dtoLista;
    }

    @Override
    public void eliminar(Long id) {
        if (creditorepository.existsById(id)) {
            creditorepository.deleteById(id);
        } else {
            throw new RuntimeException("No se encontró el Crédito con ID: " + id);
        }
    }

    @Override
    public CreditoDto actualizar(CreditoDto creditodto) {
        Long id = creditodto.getId_credito();
        if (id == null) {
            throw new RuntimeException("El ID del crédito no puede ser nulo");
        }

        Credito creditoExistente = creditorepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el crédito con ID: " + id));

        creditoExistente.setMeses_gracia(creditodto.getMeses_gracia());
        creditoExistente.setMonto_principal(creditodto.getMonto_principal());
        creditoExistente.setTasa_anual(creditodto.getTasa_anual());
        creditoExistente.setPlazo_meses(creditodto.getPlazo_meses());
        creditoExistente.setFecha_desembolso(creditodto.getFecha_desembolso());
        creditoExistente.setNumero_contrato(creditodto.getNumero_contrato());
        creditoExistente.setEstado(creditodto.getEstado());

        if (creditodto.getClientedto() != null && creditodto.getClientedto().getId_cliente() != null) {
            Cliente cliente = clienterepository.findById(creditodto.getClientedto().getId_cliente())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            creditoExistente.setCliente(cliente);
        }

        if (creditodto.getUnidaddto() != null && creditodto.getUnidaddto().getId_unidad() != null) {
            UnidadInmobiliaria unidad = unidadrepository.findById(creditodto.getUnidaddto().getId_unidad())
                    .orElseThrow(() -> new RuntimeException("Unidad inmobiliaria no encontrada"));
            creditoExistente.setUnidad(unidad);
        }

        if (creditodto.getEntidaddto() != null && creditodto.getEntidaddto().getId_entidad() != null) {
            EntidadFinanciera entidad = entidadrepository.findById(creditodto.getEntidaddto().getId_entidad())
                    .orElseThrow(() -> new RuntimeException("Entidad financiera no encontrada"));
            creditoExistente.setEntidad(entidad);
        }

        if (creditodto.getMonedadto() != null && creditodto.getMonedadto().getId_moneda() != null) {
            Moneda moneda = monedarepository.findById(creditodto.getMonedadto().getId_moneda())
                    .orElseThrow(() -> new RuntimeException("Moneda no encontrada"));
            creditoExistente.setMoneda(moneda);
        }

        if (creditodto.getTasadto() != null && creditodto.getTasadto().getId_tasa() != null) {
            TipoTasaInteres tasa = tasarepository.findById(creditodto.getTasadto().getId_tasa())
                    .orElseThrow(() -> new RuntimeException("Tasa no encontrada"));
            creditoExistente.setTasa(tasa);
        }

        if (creditodto.getCapitalizaciondto() != null && creditodto.getCapitalizaciondto().getId_capitalizacion() != null) {
            Capitalizacion capitalizacion = capitalizacionrepository.findById(creditodto.getCapitalizaciondto().getId_capitalizacion())
                    .orElseThrow(() -> new RuntimeException("Capitalización no encontrada"));
            creditoExistente.setCapitalizacion(capitalizacion);
        }

        if (creditodto.getGraciadto() != null && creditodto.getGraciadto().getId_gracia() != null) {
            PeriodoGracia gracia = graciarepository.findById(creditodto.getGraciadto().getId_gracia())
                    .orElseThrow(() -> new RuntimeException("Periodo de gracia no encontrado"));
            creditoExistente.setGracia(gracia);
        }

        Credito actualizado = creditorepository.save(creditoExistente);
        return modelMapper.map(actualizado, CreditoDto.class);
    }

    @Override
    public CreditoDto obtenerPorId(Long id) {
        Credito credito = creditorepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Crédito no encontrado con ID: " + id));

        CreditoDto dto = modelMapper.map(credito, CreditoDto.class);

        if (credito.getCliente() != null) {
            dto.setClientedto(modelMapper.map(credito.getCliente(), ClienteDto.class));
        }
        if (credito.getUnidad() != null) {
            dto.setUnidaddto(modelMapper.map(credito.getUnidad(), UnidadInmobiliariaDto.class));
        }
        if (credito.getEntidad() != null) {
            dto.setEntidaddto(modelMapper.map(credito.getEntidad(), EntidadFinancieraDto.class));
        }
        if (credito.getMoneda() != null) {
            dto.setMonedadto(modelMapper.map(credito.getMoneda(), MonedaDto.class));
        }
        if (credito.getTasa() != null) {
            dto.setTasadto(modelMapper.map(credito.getTasa(), TipoTasaInteresDto.class));
        }
        if (credito.getCapitalizacion() != null) {
            dto.setCapitalizaciondto(modelMapper.map(credito.getCapitalizacion(), CapitalizacionDto.class));
        }
        if (credito.getGracia() != null) {
            dto.setGraciadto(modelMapper.map(credito.getGracia(), PeriodoGraciaDto.class));
        }

        return dto;
    }
}
