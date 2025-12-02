package com.example.finanzas_back.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.finanzas_back.interfaces.IPagoManagementService;
import com.example.finanzas_back.dtos.*;
import com.example.finanzas_back.entities.*;
import com.example.finanzas_back.repositories.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PagoManagementService implements IPagoManagementService {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private CuotaRepository cuotaRepository;

    @Autowired
    private PagoCuotaRepository pagoCuotaRepository;

    @Autowired
    private CreditoRepository creditoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ModelMapper modelMapper;

    private static final Double TASA_MORA_DIARIA = 0.0005; // 0.05% diario
    private static final Double TASA_INTERES_COMPENSATORIO = 0.0002; // 0.02% diario
    private static final Double PORCENTAJE_ENDEUDAMIENTO_MAXIMO = 0.40; // 40% del ingreso

    @Override
    @Transactional
    public ResultadoPagoDto registrarPago(PagoDto pagoDto, List<Long> idsCuotas) {
        try {
            Pago pago = modelMapper.map(pagoDto, Pago.class);
            Pago pagoGuardado = pagoRepository.save(pago);
            PagoDto pagoGuardadoDto = modelMapper.map(pagoGuardado, PagoDto.class);

            List<PagoCuotaDto> aplicaciones = aplicarPagoACuotas(
                    pagoGuardado.getId_pago(),
                    idsCuotas,
                    pagoDto.getMonto()
            );

            Double montoAplicado = aplicaciones.stream()
                    .mapToDouble(PagoCuotaDto::getMonto_aplicado)
                    .sum();
            Double montoExcedente = pagoDto.getMonto() - montoAplicado;

            actualizarEstadoCuotas(idsCuotas);

            ResultadoPagoDto resultado = new ResultadoPagoDto();
            resultado.setPago(pagoGuardadoDto);
            resultado.setAplicaciones(aplicaciones);
            resultado.setMontoAplicado(montoAplicado);
            resultado.setMontoExcedente(montoExcedente > 0 ? montoExcedente : 0.0);
            resultado.setMensaje("Pago registrado exitosamente");
            resultado.setExito(true);

            return resultado;

        } catch (Exception e) {
            throw new RuntimeException("Error al registrar pago: " + e.getMessage(), e);
        }
    }

    @Override
    public List<PagoCuotaDto> aplicarPagoACuotas(Long idPago, List<Long> idsCuotas, Double montoTotal) {
        List<PagoCuotaDto> aplicaciones = new ArrayList<>();
        Double montoRestante = montoTotal;

        List<Cuota> cuotas = cuotaRepository.findAllById(idsCuotas);
        cuotas.sort((c1, c2) -> c1.getFecha_vencimiento().compareTo(c2.getFecha_vencimiento()));

        for (Cuota cuota : cuotas) {
            if (montoRestante <= 0) break;

            Double deudaCuota = calcularDeudaCuota(cuota);

            Double montoAplicar = Math.min(deudaCuota, montoRestante);

            PagoCuota pagoCuota = new PagoCuota();
            pagoCuota.setIdPago(idPago);
            pagoCuota.setIdCuota(cuota.getId_cuota());
            pagoCuota.setMonto_aplicado(montoAplicar);
            pagoCuota.setFecha_pago(LocalDate.now());

            PagoCuota guardado = pagoCuotaRepository.save(pagoCuota);

            actualizarSaldoCuota(cuota, montoAplicar);

            PagoCuotaDto aplicacionDto = modelMapper.map(guardado, PagoCuotaDto.class);
            aplicaciones.add(aplicacionDto);

            montoRestante -= montoAplicar;
        }

        if (montoRestante > 0) {
            aplicarPagoAdelantado(idPago, cuotas.get(0).getCredito().getId_credito(), montoRestante);
        }

        return aplicaciones;
    }

    @Override
    public Double calcularMora(CuotaDto cuotaDto, LocalDate fechaPago) {
        if (cuotaDto.getFecha_vencimiento() == null || fechaPago == null) {
            return 0.0;
        }

        if (!fechaPago.isAfter(cuotaDto.getFecha_vencimiento())) {
            return 0.0;
        }

        long diasMora = ChronoUnit.DAYS.between(cuotaDto.getFecha_vencimiento(), fechaPago);

        Double capitalPendiente = cuotaDto.getCapital_programado() != null ?
                cuotaDto.getCapital_programado() : 0.0;

        return capitalPendiente * TASA_MORA_DIARIA * diasMora;
    }

    @Override
    public Double calcularInteresCompensatorio(CuotaDto cuotaDto, LocalDate fechaPago) {
        if (cuotaDto.getFecha_vencimiento() == null || fechaPago == null) {
            return 0.0;
        }

        if (!fechaPago.isAfter(cuotaDto.getFecha_vencimiento())) {
            return 0.0;
        }

        long diasRetraso = ChronoUnit.DAYS.between(cuotaDto.getFecha_vencimiento(), fechaPago);

        Double totalCuota = cuotaDto.getTotal_cuota() != null ?
                cuotaDto.getTotal_cuota() : 0.0;

        return totalCuota * TASA_INTERES_COMPENSATORIO * diasRetraso;
    }

    @Override
    public List<CuotaDto> recalcularCronogramaDespuesPago(Long idCredito, Double montoAdelantado) {
        Credito credito = creditoRepository.findById(idCredito)
                .orElseThrow(() -> new RuntimeException("Crédito no encontrado"));

        List<Cuota> cuotasPendientes = cuotaRepository.findByCreditoIdAndEstado(idCredito, "pendiente");

        if (cuotasPendientes.isEmpty() || montoAdelantado <= 0) {
            return new ArrayList<>();
        }

        List<CuotaDto> cronogramaRecalculado = new ArrayList<>();
        Double montoRestante = montoAdelantado;

        for (Cuota cuota : cuotasPendientes) {
            if (montoRestante <= 0) break;

            CuotaDto cuotaDto = modelMapper.map(cuota, CuotaDto.class);

            Double capitalCuota = cuota.getCapital_programado() != null ?
                    cuota.getCapital_programado() : 0.0;

            Double montoAplicar = Math.min(capitalCuota, montoRestante);

            cuotaDto.setCapital_programado(capitalCuota - montoAplicar);

            Double nuevoSaldo = cuota.getSaldo_inicial() - montoAplicar;
            if (nuevoSaldo < 0) nuevoSaldo = 0.0;


            cronogramaRecalculado.add(cuotaDto);
            montoRestante -= montoAplicar;
        }

        return cronogramaRecalculado;
    }

    @Override
    public EstadoCuentaDto generarEstadoCuenta(Long idCredito, LocalDate fechaCorte) {
        Credito credito = creditoRepository.findById(idCredito)
                .orElseThrow(() -> new RuntimeException("Crédito no encontrado"));

        CreditoDto creditoDto = modelMapper.map(credito, CreditoDto.class);

        List<Cuota> todasCuotas = cuotaRepository.findByCreditoId(idCredito);
        List<CuotaDto> cuotasVencidas = new ArrayList<>();
        List<CuotaDto> cuotasPendientes = new ArrayList<>();

        for (Cuota cuota : todasCuotas) {
            CuotaDto cuotaDto = modelMapper.map(cuota, CuotaDto.class);

            if ("vencida".equals(cuota.getEstado())) {
                cuotasVencidas.add(cuotaDto);
            } else if ("pendiente".equals(cuota.getEstado())) {
                cuotasPendientes.add(cuotaDto);
            }
        }

        LocalDate fechaInicio = fechaCorte.minusMonths(1);

        List<Pago> todosPagos = pagoRepository.findByCreditoId(idCredito);
        List<Pago> pagos = todosPagos.stream()
                .filter(p -> !p.getFecha_pago().isBefore(fechaInicio) && !p.getFecha_pago().isAfter(fechaCorte))
                .collect(Collectors.toList());

        List<PagoDto> pagosDto = pagos.stream()
                .map(pago -> modelMapper.map(pago, PagoDto.class))
                .toList();

        Double saldoCapital = calcularSaldoCapital(todasCuotas);
        Double saldoInteres = calcularSaldoInteres(todasCuotas);
        Double saldoMora = calcularSaldoMora(cuotasVencidas);
        Double totalDeuda = saldoCapital + saldoInteres + saldoMora;

        ResumenPagosDto resumen = calcularResumenPagos(todasCuotas);

        EstadoCuentaDto estadoCuenta = new EstadoCuentaDto();
        estadoCuenta.setCredito(creditoDto);
        estadoCuenta.setFechaCorte(fechaCorte);
        estadoCuenta.setSaldoCapital(saldoCapital);
        estadoCuenta.setSaldoInteres(saldoInteres);
        estadoCuenta.setSaldoMora(saldoMora);
        estadoCuenta.setTotalDeuda(totalDeuda);
        estadoCuenta.setCuotasVencidas(cuotasVencidas);
        estadoCuenta.setCuotasPendientes(cuotasPendientes);
        estadoCuenta.setPagosUltimoMes(pagosDto);
        estadoCuenta.setResumen(resumen);

        return estadoCuenta;
    }

    @Override
    public Boolean verificarCapacidadPago(Long idCliente, Double montoCuotaPropuesta) {
        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        Double ingresoMensual = cliente.getIngreso_mensual();
        if (ingresoMensual == null || ingresoMensual <= 0) {
            return false;
        }

        Double cuotaMaxima = calcularCuotaMaxima(ingresoMensual, PORCENTAJE_ENDEUDAMIENTO_MAXIMO);

        return montoCuotaPropuesta <= cuotaMaxima;
    }

    @Override
    public Double calcularCuotaMaxima(Double ingresoMensual, Double porcentajeEndeudamiento) {
        if (ingresoMensual == null || ingresoMensual <= 0) {
            return 0.0;
        }

        if (porcentajeEndeudamiento == null || porcentajeEndeudamiento <= 0) {
            porcentajeEndeudamiento = PORCENTAJE_ENDEUDAMIENTO_MAXIMO;
        }

        return ingresoMensual * porcentajeEndeudamiento;
    }

    private Double calcularDeudaCuota(Cuota cuota) {
        Double deuda = 0.0;

        if (cuota.getCapital_programado() != null && cuota.getEstado().equals("pendiente")) {
            deuda += cuota.getCapital_programado();
        }

        if (cuota.getInteres_programado() != null) {
            deuda += cuota.getInteres_programado();
        }

        if (cuota.getOtros_cargos() != null) {
            deuda += cuota.getOtros_cargos();
        }

        if (cuota.getEstado().equals("vencida")) {
            CuotaDto cuotaDto = modelMapper.map(cuota, CuotaDto.class);
            Double mora = calcularMora(cuotaDto, LocalDate.now());
            Double interesCompensatorio = calcularInteresCompensatorio(cuotaDto, LocalDate.now());
            deuda += mora + interesCompensatorio;
        }

        return deuda;
    }

    private void actualizarSaldoCuota(Cuota cuota, Double montoAplicado) {
        Double deudaActual = calcularDeudaCuota(cuota);

        if (montoAplicado >= deudaActual) {
            cuota.setEstado("pagada");
            cuota.setSaldo_final(0.0);
        } else {
            cuota.setEstado("parcial");
        }

        cuotaRepository.save(cuota);
    }

    private void actualizarEstadoCuotas(List<Long> idsCuotas) {
        for (Long idCuota : idsCuotas) {
            Cuota cuota = cuotaRepository.findById(idCuota)
                    .orElseThrow(() -> new RuntimeException("Cuota no encontrada: " + idCuota));

            Double deuda = calcularDeudaCuota(cuota);
            if (deuda <= 0) {
                cuota.setEstado("pagada");
                cuotaRepository.save(cuota);
            }
        }
    }

    private void aplicarPagoAdelantado(Long idPago, Long idCredito, Double montoAdelantado) {
        List<Cuota> cuotasPendientes = cuotaRepository.findByCreditoIdAndEstado(idCredito, "pendiente");

        if (!cuotasPendientes.isEmpty()) {
            Cuota proximaCuota = cuotasPendientes.get(0);

            PagoCuota pagoAdelantado = new PagoCuota();
            pagoAdelantado.setIdPago(idPago);
            pagoAdelantado.setIdCuota(proximaCuota.getId_cuota());
            pagoAdelantado.setMonto_aplicado(montoAdelantado);
            pagoAdelantado.setFecha_pago(LocalDate.now());

            pagoCuotaRepository.save(pagoAdelantado);

            proximaCuota.setEstado("adelantado");
            cuotaRepository.save(proximaCuota);
        }
    }

    private Double calcularSaldoCapital(List<Cuota> cuotas) {
        return cuotas.stream()
                .filter(c -> "pendiente".equals(c.getEstado()) || "vencida".equals(c.getEstado()))
                .mapToDouble(c -> c.getCapital_programado() != null ? c.getCapital_programado() : 0.0)
                .sum();
    }

    private Double calcularSaldoInteres(List<Cuota> cuotas) {
        return cuotas.stream()
                .filter(c -> "pendiente".equals(c.getEstado()) || "vencida".equals(c.getEstado()))
                .mapToDouble(c -> c.getInteres_programado() != null ? c.getInteres_programado() : 0.0)
                .sum();
    }

    private Double calcularSaldoMora(List<CuotaDto> cuotasVencidas) {
        return cuotasVencidas.stream()
                .mapToDouble(cuota -> calcularMora(cuota, LocalDate.now()))
                .sum();
    }

    private ResumenPagosDto calcularResumenPagos(List<Cuota> todasCuotas) {
        ResumenPagosDto resumen = new ResumenPagosDto();

        int total = todasCuotas.size();
        int pagadas = (int) todasCuotas.stream().filter(c -> "pagada".equals(c.getEstado())).count();
        int pendientes = (int) todasCuotas.stream().filter(c -> "pendiente".equals(c.getEstado())).count();
        int vencidas = (int) todasCuotas.stream().filter(c -> "vencida".equals(c.getEstado())).count();

        Double totalPagado = todasCuotas.stream()
                .filter(c -> "pagada".equals(c.getEstado()))
                .mapToDouble(c -> c.getTotal_cuota() != null ? c.getTotal_cuota() : 0.0)
                .sum();

        Double totalPendiente = todasCuotas.stream()
                .filter(c -> "pendiente".equals(c.getEstado()) || "vencida".equals(c.getEstado()))
                .mapToDouble(c -> c.getTotal_cuota() != null ? c.getTotal_cuota() : 0.0)
                .sum();

        Double porcentajeAvance = total > 0 ? (pagadas * 100.0) / total : 0.0;

        resumen.setTotalCuotas(total);
        resumen.setCuotasPagadas(pagadas);
        resumen.setCuotasPendientes(pendientes);
        resumen.setCuotasVencidas(vencidas);
        resumen.setTotalPagado(totalPagado);
        resumen.setTotalPendiente(totalPendiente);
        resumen.setPorcentajeAvance(porcentajeAvance);

        return resumen;
    }
}
