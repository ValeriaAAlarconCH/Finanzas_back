package com.example.finanzas_back.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.finanzas_back.interfaces.IValidacionService;
import com.example.finanzas_back.dtos.*;
import com.example.finanzas_back.entities.*;
import com.example.finanzas_back.repositories.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Service
public class ValidacionService implements IValidacionService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private CreditoRepository creditoRepository;

    @Autowired
    private UnidadInmobiliariaRepository unidadRepository;

    @Autowired
    private ModelMapper modelMapper;

    private static final Integer EDAD_MINIMA = 18;
    private static final Integer EDAD_MAXIMA = 70;
    private static final Double PORCENTAJE_ENDEUDAMIENTO_MAXIMO = 0.40; // 40%
    private static final Double MONTO_MINIMO_CREDITO = 10000.0;
    private static final Double MONTO_MAXIMO_CREDITO = 1000000.0;
    private static final Integer PLAZO_MINIMO = 12; // meses
    private static final Integer PLAZO_MAXIMO = 360; // meses
    private static final Double TASA_MINIMA = 1.0; // 1%
    private static final Double TASA_MAXIMA = 30.0; // 30%

    @Override
    public ResultadoValidacionDto validarCliente(ClienteDto cliente) {
        ResultadoValidacionDto resultado = new ResultadoValidacionDto(true, "Cliente válido");

        if (cliente.getDni() == null || cliente.getDni().toString().length() != 8) {
            resultado.agregarError("DNI inválido. Debe tener 8 dígitos");
        }

        if (cliente.getFecha_nacimiento() != null) {
            int edad = Period.between(cliente.getFecha_nacimiento(), LocalDate.now()).getYears();
            if (edad < EDAD_MINIMA) {
                resultado.agregarError("El cliente debe tener al menos " + EDAD_MINIMA + " años");
            }
            if (edad > EDAD_MAXIMA) {
                resultado.agregarError("El cliente no debe exceder los " + EDAD_MAXIMA + " años");
            }
        }

        if (cliente.getIngreso_mensual() == null || cliente.getIngreso_mensual() <= 0) {
            resultado.agregarError("Ingreso mensual inválido");
        } else if (cliente.getIngreso_mensual() < 1500) {
            resultado.agregarAdvertencia("Ingreso mensual bajo para crédito hipotecario");
        }

        if (cliente.getEmail() == null || !cliente.getEmail().contains("@")) {
            resultado.agregarError("Email inválido");
        }

        if (cliente.getTelefono() != null && cliente.getTelefono().toString().length() != 9) {
            resultado.agregarAdvertencia("Teléfono puede ser inválido");
        }

        return resultado;
    }

    @Override
    public ResultadoValidacionDto validarCapacidadPago(ClienteDto cliente, Double montoCuota) {
        ResultadoValidacionDto resultado = new ResultadoValidacionDto(true, "Capacidad de pago válida");

        if (cliente.getIngreso_mensual() == null || montoCuota == null) {
            resultado.agregarError("Datos insuficientes para validar capacidad de pago");
            return resultado;
        }

        Double ingreso = cliente.getIngreso_mensual();
        Double cuotaMaxima = ingreso * PORCENTAJE_ENDEUDAMIENTO_MAXIMO;

        Integer puntaje = 0;

        if (montoCuota > cuotaMaxima) {
            resultado.agregarError("La cuota excede el " + (PORCENTAJE_ENDEUDAMIENTO_MAXIMO * 100) +
                    "% del ingreso mensual");
            puntaje += 50;
        }

        if (cliente.getNum_dependientes() != null && cliente.getNum_dependientes() > 3) {
            resultado.agregarAdvertencia("Número alto de dependientes puede afectar capacidad de pago");
            puntaje += 10;
        }

        resultado.setPuntaje(puntaje);
        return resultado;
    }

    @Override
    public ResultadoValidacionDto validarHistorialCrediticio(Long idCliente) {
        ResultadoValidacionDto resultado = new ResultadoValidacionDto(true, "Historial crediticio válido");

        List<Credito> creditosCliente = creditoRepository.findByClienteId(idCliente);

        if (creditosCliente != null && !creditosCliente.isEmpty()) {
            long creditosVencidos = creditosCliente.stream()
                    .filter(c -> "vencido".equals(c.getEstado()))
                    .count();

            long creditosCancelados = creditosCliente.stream()
                    .filter(c -> "cancelado".equals(c.getEstado()))
                    .count();

            Integer puntaje = 0;

            if (creditosVencidos > 0) {
                resultado.agregarAdvertencia("El cliente tiene " + creditosVencidos + " crédito(s) vencido(s)");
                puntaje += (int) creditosVencidos * 20;
            }

            if (creditosCancelados > 2) {
                resultado.agregarAdvertencia("El cliente tiene múltiples créditos cancelados");
                puntaje += 30;
            }

            long creditosActivos = creditosCliente.stream()
                    .filter(c -> "activo".equals(c.getEstado()))
                    .count();

            if (creditosActivos > 3) {
                resultado.agregarAdvertencia("El cliente tiene múltiples créditos activos");
                puntaje += 15;
            }

            resultado.setPuntaje(puntaje);

            if (puntaje > 50) {
                resultado.agregarError("Historial crediticio de alto riesgo");
                resultado.setValido(false);
            }
        }

        return resultado;
    }

    @Override
    public ResultadoValidacionDto validarCredito(CreditoDto credito) {
        ResultadoValidacionDto resultado = new ResultadoValidacionDto(true, "Crédito válido");

        ResultadoValidacionDto validacionMonto = validarMontoCredito(credito, credito.getClientedto());
        if (!validacionMonto.getValido()) {
            resultado.getErrores().addAll(validacionMonto.getErrores());
            resultado.setValido(false);
        }
        resultado.getAdvertencias().addAll(validacionMonto.getAdvertencias());

        ResultadoValidacionDto validacionTasas = validarTasas(credito);
        if (!validacionTasas.getValido()) {
            resultado.getErrores().addAll(validacionTasas.getErrores());
            resultado.setValido(false);
        }

        ResultadoValidacionDto validacionPlazo = validarPlazo(credito);
        if (!validacionPlazo.getValido()) {
            resultado.getErrores().addAll(validacionPlazo.getErrores());
            resultado.setValido(false);
        }

        Integer puntajeTotal = validacionMonto.getPuntaje() +
                validacionTasas.getPuntaje() +
                validacionPlazo.getPuntaje();
        resultado.setPuntaje(puntajeTotal);

        if (credito.getFecha_desembolso() != null &&
                credito.getFecha_desembolso().isBefore(LocalDate.now())) {
            resultado.agregarError("Fecha de desembolso no puede ser en el pasado");
        }

        if (credito.getNumero_contrato() == null || credito.getNumero_contrato().isEmpty()) {
            resultado.agregarError("Número de contrato requerido");
        }

        return resultado;
    }

    @Override
    public ResultadoValidacionDto validarMontoCredito(CreditoDto credito, ClienteDto cliente) {
        ResultadoValidacionDto resultado = new ResultadoValidacionDto(true, "Monto válido");
        Integer puntaje = 0;

        if (credito.getMonto_principal() == null) {
            resultado.agregarError("Monto principal requerido");
            return resultado;
        }

        Double monto = credito.getMonto_principal();

        if (monto < MONTO_MINIMO_CREDITO) {
            resultado.agregarError("Monto mínimo del crédito es S/ " + MONTO_MINIMO_CREDITO);
            puntaje += 30;
        }

        if (monto > MONTO_MAXIMO_CREDITO) {
            resultado.agregarError("Monto máximo del crédito es S/ " + MONTO_MAXIMO_CREDITO);
            puntaje += 50;
        }

        if (cliente != null && cliente.getIngreso_mensual() != null) {
            Double ingresoAnual = cliente.getIngreso_mensual() * 12;
            Double relacionMontoIngreso = monto / ingresoAnual;

            if (relacionMontoIngreso > 10) { // No más de 10 veces el ingreso anual
                resultado.agregarError("El monto excede 10 veces el ingreso anual del cliente");
                puntaje += 40;
            } else if (relacionMontoIngreso > 8) {
                resultado.agregarAdvertencia("Monto elevado en relación al ingreso");
                puntaje += 20;
            }
        }

        resultado.setPuntaje(puntaje);
        return resultado;
    }

    @Override
    public ResultadoValidacionDto validarTasas(CreditoDto credito) {
        ResultadoValidacionDto resultado = new ResultadoValidacionDto(true, "Tasas válidas");
        Integer puntaje = 0;

        if (credito.getTasa_anual() == null) {
            resultado.agregarError("Tasa anual requerida");
            return resultado;
        }

        Double tasa = credito.getTasa_anual();

        if (tasa < TASA_MINIMA) {
            resultado.agregarError("Tasa mínima permitida es " + TASA_MINIMA + "%");
            puntaje += 30;
        }

        if (tasa > TASA_MAXIMA) {
            resultado.agregarError("Tasa máxima permitida es " + TASA_MAXIMA + "%");
            puntaje += 50;
        }

        if (credito.getTasadto() != null) {
            String tipoTasa = credito.getTasadto().getTipo();

            if ("nominal".equals(tipoTasa) && tasa > 20) {
                resultado.agregarAdvertencia("Tasa nominal elevada");
                puntaje += 15;
            }

            if ("efectiva".equals(tipoTasa) && tasa > 25) {
                resultado.agregarAdvertencia("Tasa efectiva elevada");
                puntaje += 20;
            }
        }

        resultado.setPuntaje(puntaje);
        return resultado;
    }

    @Override
    public ResultadoValidacionDto validarPlazo(CreditoDto credito) {
        ResultadoValidacionDto resultado = new ResultadoValidacionDto(true, "Plazo válido");
        Integer puntaje = 0;

        if (credito.getPlazo_meses() == null) {
            resultado.agregarError("Plazo requerido");
            return resultado;
        }

        Integer plazo = credito.getPlazo_meses();

        if (plazo < PLAZO_MINIMO) {
            resultado.agregarError("Plazo mínimo es " + PLAZO_MINIMO + " meses");
            puntaje += 30;
        }

        if (plazo > PLAZO_MAXIMO) {
            resultado.agregarError("Plazo máximo es " + PLAZO_MAXIMO + " meses");
            puntaje += 40;
        }

        if (credito.getClientedto() != null &&
                credito.getClientedto().getFecha_nacimiento() != null) {

            LocalDate fechaNacimiento = credito.getClientedto().getFecha_nacimiento();
            int edadActual = Period.between(fechaNacimiento, LocalDate.now()).getYears();
            int edadFinal = edadActual + (plazo / 12);

            if (edadFinal > 75) {
                resultado.agregarAdvertencia("Al final del crédito el cliente tendría " + edadFinal + " años");
                puntaje += 25;
            }
        }

        resultado.setPuntaje(puntaje);
        return resultado;
    }

    @Override
    public ResultadoValidacionDto validarUnidadInmobiliaria(Long idUnidad) {
        ResultadoValidacionDto resultado = new ResultadoValidacionDto(true, "Unidad inmobiliaria válida");

        UnidadInmobiliaria unidad = unidadRepository.findById(idUnidad).orElse(null);

        if (unidad == null) {
            resultado.agregarError("Unidad inmobiliaria no encontrada");
            return resultado;
        }

        if (!"disponible".equals(unidad.getEstado()) && !"vendido".equals(unidad.getEstado())) {
            resultado.agregarError("La unidad no está disponible para venta");
        }

        if (unidad.getPrecio_venta() == null || unidad.getPrecio_venta() <= 0) {
            resultado.agregarError("Precio de venta inválido");
        }

        if (unidad.getProyecto() == null) {
            resultado.agregarAdvertencia("Unidad sin proyecto asociado");
        }

        return resultado;
    }

    @Override
    public ResultadoValidacionDto validarDocumentosCliente(Long idCliente) {
        ResultadoValidacionDto resultado = new ResultadoValidacionDto(true, "Documentos válidos");

        Cliente cliente = clienteRepository.findById(idCliente).orElse(null);

        if (cliente == null) {
            resultado.agregarError("Cliente no encontrado");
            return resultado;
        }

        List<String> documentosRequeridos = new ArrayList<>();

        if (cliente.getDni() == null) {
            documentosRequeridos.add("DNI");
        }

        if (cliente.getIngreso_mensual() == null) {
            documentosRequeridos.add("Comprobante de ingresos");
        }

        if (cliente.getOcupacion() == null || cliente.getOcupacion().isEmpty()) {
            documentosRequeridos.add("Constancia de trabajo");
        }

        if (!documentosRequeridos.isEmpty()) {
            resultado.agregarError("Documentos faltantes: " + String.join(", ", documentosRequeridos));
        }

        return resultado;
    }

    @Override
    public ResultadoValidacionDto validarCompletitudSolicitud(CreditoDto credito) {
        ResultadoValidacionDto resultado = new ResultadoValidacionDto(true, "Solicitud completa");

        if (credito.getClientedto() == null) {
            resultado.agregarError("Datos del cliente requeridos");
        }

        if (credito.getUnidaddto() == null) {
            resultado.agregarError("Unidad inmobiliaria requerida");
        }

        if (credito.getEntidaddto() == null) {
            resultado.agregarError("Entidad financiera requerida");
        }

        if (credito.getMonedadto() == null) {
            resultado.agregarError("Moneda requerida");
        }

        if (credito.getTasadto() == null) {
            resultado.agregarError("Tipo de tasa requerido");
        }

        if (credito.getCapitalizaciondto() == null) {
            resultado.agregarError("Capitalización requerida");
        }

        if (credito.getGraciadto() == null) {
            resultado.agregarAdvertencia("Periodo de gracia no especificado (se usará valor por defecto)");
        }

        if (credito.getClientedto() != null && credito.getClientedto().getId_cliente() == null) {
            resultado.agregarError("ID del cliente requerido");
        }

        if (credito.getUnidaddto() != null && credito.getUnidaddto().getId_unidad() == null) {
            resultado.agregarError("ID de la unidad inmobiliaria requerido");
        }

        if (credito.getEntidaddto() != null && credito.getEntidaddto().getId_entidad() == null) {
            resultado.agregarError("ID de la entidad financiera requerido");
        }

        return resultado;
    }
}