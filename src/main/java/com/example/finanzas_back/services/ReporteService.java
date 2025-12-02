package com.example.finanzas_back.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.finanzas_back.interfaces.IReporteService;
import com.example.finanzas_back.dtos.*;
import com.example.finanzas_back.entities.*;
import com.example.finanzas_back.repositories.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReporteService implements IReporteService {

    @Autowired
    private CreditoRepository creditoRepository;

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private CuotaRepository cuotaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EntidadFinancieraRepository entidadRepository;

    @Autowired
    private ProyectoInmobiliarioRepository proyectoRepository;

    @Autowired
    private UnidadInmobiliariaRepository unidadInmobiliariaRepository; // CAMBIO DE NOMBRE

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ReporteCreditosDto generarReporteCreditos(LocalDate fechaInicio, LocalDate fechaFin) {
        ReporteCreditosDto reporte = new ReporteCreditosDto();
        reporte.setFechaGeneracion(LocalDate.now());
        reporte.setPeriodoInicio(fechaInicio);
        reporte.setPeriodoFin(fechaFin);

        List<Credito> creditos = creditoRepository.findAll();

        if (fechaInicio != null && fechaFin != null) {
            creditos = creditos.stream()
                    .filter(c -> !c.getFecha_desembolso().isBefore(fechaInicio) &&
                            !c.getFecha_desembolso().isAfter(fechaFin))
                    .collect(Collectors.toList());
        }

        int total = creditos.size();
        int aprobados = (int) creditos.stream().filter(c -> "aprobado".equals(c.getEstado())).count();
        int rechazados = (int) creditos.stream().filter(c -> "rechazado".equals(c.getEstado())).count();
        int activos = (int) creditos.stream().filter(c -> "activo".equals(c.getEstado())).count();
        int cancelados = (int) creditos.stream().filter(c -> "cancelado".equals(c.getEstado())).count();

        Double montoDesembolsado = creditos.stream()
                .mapToDouble(Credito::getMonto_principal)
                .sum();

        Double montoRecuperado = 0.0;
        for (Credito credito : creditos) {
            Double sumPagos = pagoRepository.sumMontoByCreditoId(credito.getId_credito());
            if (sumPagos != null) {
                montoRecuperado += sumPagos;
            }
        }

        Double tasaAprobacion = total > 0 ? (aprobados * 100.0) / total : 0.0;

        List<CreditoDto> creditosRecientes = creditos.stream()
                .sorted((c1, c2) -> c2.getFecha_desembolso().compareTo(c1.getFecha_desembolso()))
                .limit(10)
                .map(c -> modelMapper.map(c, CreditoDto.class))
                .collect(Collectors.toList());

        Map<String, Integer> creditosPorEntidad = new HashMap<>();
        for (Credito credito : creditos) {
            if (credito.getEntidad() != null) {
                String nombreEntidad = credito.getEntidad().getNombre();
                creditosPorEntidad.put(nombreEntidad,
                        creditosPorEntidad.getOrDefault(nombreEntidad, 0) + 1);
            }
        }

        Map<String, Double> montosPorMoneda = new HashMap<>();
        for (Credito credito : creditos) {
            if (credito.getMoneda() != null) {
                String moneda = credito.getMoneda().getNombre();
                montosPorMoneda.put(moneda,
                        montosPorMoneda.getOrDefault(moneda, 0.0) + credito.getMonto_principal());
            }
        }

        List<TrendDataDto> tendenciaMensual = calcularTendenciaCreditosMensual();

        reporte.setTotalCreditos(total);
        reporte.setCreditosAprobados(aprobados);
        reporte.setCreditosRechazados(rechazados);
        reporte.setCreditosActivos(activos);
        reporte.setCreditosCancelados(cancelados);
        reporte.setMontoTotalDesembolsado(montoDesembolsado);
        reporte.setMontoTotalRecuperado(montoRecuperado);
        reporte.setTasaAprobacion(tasaAprobacion);
        reporte.setCreditosRecientes(creditosRecientes);
        reporte.setCreditosPorEntidad(creditosPorEntidad);
        reporte.setMontosPorMoneda(montosPorMoneda);
        reporte.setTendenciaMensual(tendenciaMensual);

        return reporte;
    }

    @Override
    public ReportePagosDto generarReportePagos(LocalDate fechaInicio, LocalDate fechaFin) {
        ReportePagosDto reporte = new ReportePagosDto();
        reporte.setFechaGeneracion(LocalDate.now());
        reporte.setPeriodoInicio(fechaInicio);
        reporte.setPeriodoFin(fechaFin);

        List<Pago> pagos;
        if (fechaInicio != null && fechaFin != null) {
            pagos = pagoRepository.findByFechaBetween(fechaInicio, fechaFin);
        } else {
            pagos = pagoRepository.findAll();
        }

        int total = pagos.size();
        Double montoTotal = pagos.stream()
                .mapToDouble(Pago::getMonto)
                .sum();
        Double promedio = total > 0 ? montoTotal / total : 0.0;

        int puntuales = (int) pagos.stream()
                .filter(p -> {
                    // Verificar si el pago fue puntual
                    // Esto requeriría comparar con fechas de vencimiento
                    return true; // Simplificado por ahora
                })
                .count();
        int conMora = total - puntuales;

        Double tasaCobranza = 0.0;

        List<PagoDto> pagosRecientes = pagos.stream()
                .sorted((p1, p2) -> p2.getFecha_pago().compareTo(p1.getFecha_pago()))
                .limit(10)
                .map(p -> modelMapper.map(p, PagoDto.class))
                .collect(Collectors.toList());

        Map<String, Integer> pagosPorMetodo = new HashMap<>();
        for (Pago pago : pagos) {
            String metodo = pago.getMetodo_pago();
            if (metodo != null) {
                pagosPorMetodo.put(metodo,
                        pagosPorMetodo.getOrDefault(metodo, 0) + 1);
            }
        }

        Map<String, Double> pagosPorDia = new HashMap<>();
        String[] dias = {"LUNES", "MARTES", "MIÉRCOLES", "JUEVES", "VIERNES", "SÁBADO", "DOMINGO"};
        for (String dia : dias) {
            pagosPorDia.put(dia, 0.0);
        }

        for (Pago pago : pagos) {
            String diaSemana = pago.getFecha_pago().getDayOfWeek().toString();
            Double montoActual = pagosPorDia.getOrDefault(diaSemana, 0.0);
            pagosPorDia.put(diaSemana, montoActual + pago.getMonto());
        }

        List<TopClienteDto> topClientes = calcularTopClientesPagos(pagos);

        reporte.setTotalPagos(total);
        reporte.setMontoTotalPagado(montoTotal);
        reporte.setPromedioPago(promedio);
        reporte.setPagosPuntuales(puntuales);
        reporte.setPagosConMora(conMora);
        reporte.setTasaCobranza(tasaCobranza);
        reporte.setPagosRecientes(pagosRecientes);
        reporte.setPagosPorMetodo(pagosPorMetodo);
        reporte.setPagosPorDia(pagosPorDia);
        reporte.setTopClientesPagos(topClientes);

        return reporte;
    }

    @Override
    public DashboardDto obtenerDashboard() {
        DashboardDto dashboard = new DashboardDto();

        List<Cliente> clientes = clienteRepository.findAll();
        List<Credito> creditos = creditoRepository.findAll();
        List<Cuota> cuotas = cuotaRepository.findAll();

        dashboard.setTotalClientes(clientes.size());
        dashboard.setTotalCreditos(creditos.size());

        List<Credito> creditosActivos = creditoRepository.findByEstado("activo");
        dashboard.setCreditosActivos(creditosActivos.size());

        Double carteraVigente = calcularCarteraVigente();
        Double carteraVencida = calcularCarteraVencida();
        dashboard.setCarteraVigente(carteraVigente);
        dashboard.setCarteraVencida(carteraVencida);

        LocalDate hoy = LocalDate.now();
        List<Pago> pagosHoy = pagoRepository.findByFechaBetween(hoy, hoy);
        dashboard.setPagosHoy(pagosHoy.size());

        Double montoPagadoHoy = pagosHoy.stream()
                .mapToDouble(Pago::getMonto)
                .sum();
        dashboard.setMontoPagadoHoy(montoPagadoHoy);

        List<Cuota> cuotasVencidas = cuotaRepository.findByFechaVencimientoBetween(hoy, hoy);
        cuotasVencidas = cuotasVencidas.stream()
                .filter(c -> "vencida".equals(c.getEstado()))
                .collect(Collectors.toList());

        dashboard.setCuotasVencidasHoy(cuotasVencidas.size());

        Double montoVencidoHoy = cuotasVencidas.stream()
                .mapToDouble(c -> c.getTotal_cuota() != null ? c.getTotal_cuota() : 0.0)
                .sum();
        dashboard.setMontoVencidoHoy(montoVencidoHoy);

        List<AlertaDto> alertas = generarAlertas();
        dashboard.setAlertas(alertas);

        Map<String, Double> distribucionCartera = calcularDistribucionCartera();
        dashboard.setDistribucionCartera(distribucionCartera);

        List<MetricaMensualDto> tendenciaCobranza = calcularTendenciaCobranza();
        dashboard.setTendenciaCobranza(tendenciaCobranza);

        List<MetricaMensualDto> tendenciaCreditos = calcularTendenciaCreditos();
        dashboard.setTendenciaCreditos(tendenciaCreditos);

        Map<String, Double> kpis = calcularKPIs();
        dashboard.setIndiceMorosidad(kpis.getOrDefault("indiceMorosidad", 0.0));
        dashboard.setEficienciaCobranza(kpis.getOrDefault("eficienciaCobranza", 0.0));
        dashboard.setTasaCancelacion(kpis.getOrDefault("tasaCancelacion", 0.0));
        dashboard.setRotacionCartera(kpis.getOrDefault("rotacionCartera", 0.0));

        return dashboard;
    }

    @Override
    public Map<String, Double> calcularKPIs() {
        Map<String, Double> kpis = new HashMap<>();

        Double carteraVencida = calcularCarteraVencida();
        Double carteraTotal = calcularCarteraTotal();
        Double indiceMorosidad = carteraTotal > 0 ? (carteraVencida * 100) / carteraTotal : 0.0;
        kpis.put("indiceMorosidad", indiceMorosidad);

        Double pagosUltimoMes = calcularPagosUltimoMes();
        Double cuotasVencidasUltimoMes = calcularCuotasVencidasUltimoMes();
        Double eficienciaCobranza = cuotasVencidasUltimoMes > 0 ?
                (pagosUltimoMes * 100) / cuotasVencidasUltimoMes : 100.0;
        kpis.put("eficienciaCobranza", eficienciaCobranza);

        Long creditosCancelados = creditoRepository.countCreditosActivosByCliente(1L); // Ejemplo
        Long creditosTotales = (long) creditoRepository.findAll().size();
        Double tasaCancelacion = creditosTotales > 0 ?
                (creditosCancelados * 100.0) / creditosTotales : 0.0;
        kpis.put("tasaCancelacion", tasaCancelacion);

        Double montoRecuperadoAnual = calcularMontoRecuperadoAnual();
        Double carteraPromedio = calcularCarteraPromedio();
        Double rotacionCartera = carteraPromedio > 0 ? montoRecuperadoAnual / carteraPromedio : 0.0;
        kpis.put("rotacionCartera", rotacionCartera);

        return kpis;
    }

    private Double calcularCarteraVigente() {
        List<Credito> creditosActivos = creditoRepository.findByEstado("activo");
        return creditosActivos.stream()
                .mapToDouble(Credito::getMonto_principal)
                .sum();
    }

    private Double calcularCarteraVencida() {
        List<Cuota> cuotasVencidas = cuotaRepository.findAll().stream()
                .filter(c -> "vencida".equals(c.getEstado()))
                .collect(Collectors.toList());

        return cuotasVencidas.stream()
                .mapToDouble(c -> c.getTotal_cuota() != null ? c.getTotal_cuota() : 0.0)
                .sum();
    }

    private Double calcularCarteraTotal() {
        return creditoRepository.findAll().stream()
                .mapToDouble(Credito::getMonto_principal)
                .sum();
    }

    private Double calcularPagosUltimoMes() {
        LocalDate fin = LocalDate.now();
        LocalDate inicio = fin.minusMonths(1);

        List<Pago> pagos = pagoRepository.findByFechaBetween(inicio, fin);
        return pagos.stream()
                .mapToDouble(Pago::getMonto)
                .sum();
    }

    private Double calcularCuotasVencidasUltimoMes() {
        LocalDate fin = LocalDate.now();
        LocalDate inicio = fin.minusMonths(1);

        List<Cuota> cuotas = cuotaRepository.findByFechaVencimientoBetween(inicio, fin);
        cuotas = cuotas.stream()
                .filter(c -> "vencida".equals(c.getEstado()))
                .collect(Collectors.toList());

        return cuotas.stream()
                .mapToDouble(c -> c.getTotal_cuota() != null ? c.getTotal_cuota() : 0.0)
                .sum();
    }

    private Double calcularMontoRecuperadoAnual() {
        LocalDate fin = LocalDate.now();
        LocalDate inicio = fin.minusYears(1);

        List<Pago> pagos = pagoRepository.findByFechaBetween(inicio, fin);
        return pagos.stream()
                .mapToDouble(Pago::getMonto)
                .sum();
    }

    private Double calcularCarteraPromedio() {
        List<Double> carterasMensuales = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            LocalDate mes = LocalDate.now().minusMonths(i);
            // Cálculo simplificado
            carterasMensuales.add(calcularCarteraTotal());
        }

        return carterasMensuales.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    private List<AlertaDto> generarAlertas() {
        List<AlertaDto> alertas = new ArrayList<>();

        LocalDate hoy = LocalDate.now();
        LocalDate tresDias = hoy.plusDays(3);

        List<Cuota> cuotasPorVencer = cuotaRepository.findByFechaVencimientoBetween(hoy, tresDias);
        cuotasPorVencer = cuotasPorVencer.stream()
                .filter(c -> "pendiente".equals(c.getEstado()))
                .collect(Collectors.toList());

        for (Cuota cuota : cuotasPorVencer) {
            AlertaDto alerta = new AlertaDto();
            alerta.setTipo("vencimiento");
            alerta.setMensaje("Cuota #" + cuota.getNumero_cuota() + " vence en " +
                    cuota.getFecha_vencimiento().getDayOfMonth() + "/" +
                    cuota.getFecha_vencimiento().getMonthValue());
            alerta.setPrioridad("media");
            alerta.setFecha(hoy);
            alerta.setReferenciaId(cuota.getId_cuota());
            alerta.setLeida(false);
            alertas.add(alerta);
        }

        List<Cuota> cuotasVencidas = cuotaRepository.findAll().stream()
                .filter(c -> "vencida".equals(c.getEstado()))
                .filter(c -> ChronoUnit.DAYS.between(c.getFecha_vencimiento(), hoy) > 15)
                .collect(Collectors.toList());

        for (Cuota cuota : cuotasVencidas) {
            AlertaDto alerta = new AlertaDto();
            alerta.setTipo("mora");
            alerta.setMensaje("Cuota #" + cuota.getNumero_cuota() + " con más de 15 días de mora");
            alerta.setPrioridad("alta");
            alerta.setFecha(hoy);
            alerta.setReferenciaId(cuota.getId_cuota());
            alerta.setLeida(false);
            alertas.add(alerta);
        }

        return alertas;
    }

    private Map<String, Double> calcularDistribucionCartera() {
        Map<String, Double> distribucion = new HashMap<>();

        List<Credito> creditos = creditoRepository.findAll();

        Map<String, Double> porEstado = creditos.stream()
                .collect(Collectors.groupingBy(
                        Credito::getEstado,
                        Collectors.summingDouble(Credito::getMonto_principal)
                ));

        distribucion.putAll(porEstado);

        Map<String, Double> porMoneda = creditos.stream()
                .filter(c -> c.getMoneda() != null)
                .collect(Collectors.groupingBy(
                        c -> c.getMoneda().getNombre(),
                        Collectors.summingDouble(Credito::getMonto_principal)
                ));

        distribucion.putAll(porMoneda);

        return distribucion;
    }

    private List<MetricaMensualDto> calcularTendenciaCobranza() {
        List<MetricaMensualDto> tendencia = new ArrayList<>();

        for (int i = 11; i >= 0; i--) {
            LocalDate mes = LocalDate.now().minusMonths(i);
            YearMonth yearMonth = YearMonth.from(mes);

            LocalDate inicio = yearMonth.atDay(1);
            LocalDate fin = yearMonth.atEndOfMonth();

            List<Pago> pagos = pagoRepository.findByFechaBetween(inicio, fin);
            Double monto = pagos.stream()
                    .mapToDouble(Pago::getMonto)
                    .sum();

            MetricaMensualDto metrica = new MetricaMensualDto();
            metrica.setMes(yearMonth.toString());
            metrica.setValor(monto);
            tendencia.add(metrica);
        }

        return tendencia;
    }

    private List<MetricaMensualDto> calcularTendenciaCreditos() {
        List<MetricaMensualDto> tendencia = new ArrayList<>();

        for (int i = 11; i >= 0; i--) {
            LocalDate mes = LocalDate.now().minusMonths(i);
            YearMonth yearMonth = YearMonth.from(mes);

            LocalDate inicio = yearMonth.atDay(1);
            LocalDate fin = yearMonth.atEndOfMonth();

            List<Credito> creditos = creditoRepository.findAll().stream()
                    .filter(c -> !c.getFecha_desembolso().isBefore(inicio) &&
                            !c.getFecha_desembolso().isAfter(fin))
                    .collect(Collectors.toList());

            MetricaMensualDto metrica = new MetricaMensualDto();
            metrica.setMes(yearMonth.toString());
            metrica.setValor((double) creditos.size());
            tendencia.add(metrica);
        }

        return tendencia;
    }

    private List<TrendDataDto> calcularTendenciaCreditosMensual() {
        List<TrendDataDto> tendencia = new ArrayList<>();

        for (int i = 5; i >= 0; i--) {
            LocalDate mes = LocalDate.now().minusMonths(i);
            YearMonth yearMonth = YearMonth.from(mes);

            LocalDate inicio = yearMonth.atDay(1);
            LocalDate fin = yearMonth.atEndOfMonth();

            List<Credito> creditos = creditoRepository.findAll().stream()
                    .filter(c -> !c.getFecha_desembolso().isBefore(inicio) &&
                            !c.getFecha_desembolso().isAfter(fin))
                    .collect(Collectors.toList());

            TrendDataDto data = new TrendDataDto();
            data.setPeriodo(yearMonth.toString());
            data.setCantidad(creditos.size());
            data.setMonto(creditos.stream()
                    .mapToDouble(Credito::getMonto_principal)
                    .sum());

            tendencia.add(data);
        }

        return tendencia;
    }

    private List<TopClienteDto> calcularTopClientesPagos(List<Pago> pagos) {
        Map<Long, Double> pagosPorCliente = new HashMap<>();

        for (Pago pago : pagos) {
            if (pago.getCliente() != null) {
                Long idCliente = pago.getCliente().getId_cliente();
                Double montoActual = pagosPorCliente.getOrDefault(idCliente, 0.0);
                pagosPorCliente.put(idCliente, montoActual + pago.getMonto());
            }
        }

        return pagosPorCliente.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(5)
                .map(entry -> {
                    TopClienteDto top = new TopClienteDto();
                    top.setIdCliente(entry.getKey());
                    top.setMontoPagado(entry.getValue());

                    // Obtener nombre del cliente
                    Cliente cliente = clienteRepository.findById(entry.getKey()).orElse(null);
                    if (cliente != null) {
                        top.setNombreCliente(cliente.getNombres() + " " + cliente.getApellidos());
                    }

                    return top;
                })
                .collect(Collectors.toList());
    }

    @Override
    public ReporteMorosidadDto generarReporteMorosidad() {
        ReporteMorosidadDto reporte = new ReporteMorosidadDto();
        reporte.setFechaGeneracion(LocalDate.now());

        Double carteraVencida = calcularCarteraVencida();
        Double carteraTotal = calcularCarteraTotal();

        reporte.setCarteraVencida(carteraVencida);
        reporte.setCarteraTotal(carteraTotal);
        reporte.setIndiceMorosidad(carteraTotal > 0 ? (carteraVencida * 100) / carteraTotal : 0.0);

        List<Cliente> clientes = clienteRepository.findAll();
        reporte.setTotalClientes(clientes.size());

        reporte.setClientesMorosos(5); // Ejemplo
        reporte.setTasaMorosidad(clientes.size() > 0 ? (5.0 * 100) / clientes.size() : 0.0);

        return reporte;
    }

    @Override
    public EstadisticasClienteDto obtenerEstadisticasCliente(Long idCliente) {
        EstadisticasClienteDto estadisticas = new EstadisticasClienteDto();

        Cliente cliente = clienteRepository.findById(idCliente).orElse(null);
        if (cliente != null) {
            estadisticas.setCliente(modelMapper.map(cliente, ClienteDto.class));

            List<Credito> creditos = creditoRepository.findByClienteId(idCliente);
            estadisticas.setTotalCreditos(creditos.size());

            estadisticas.setCreditosActivos((int) creditos.stream()
                    .filter(c -> "activo".equals(c.getEstado())).count());
            estadisticas.setCreditosCancelados((int) creditos.stream()
                    .filter(c -> "cancelado".equals(c.getEstado())).count());

            estadisticas.setMontoTotalPrestado(creditos.stream()
                    .mapToDouble(Credito::getMonto_principal)
                    .sum());
        }

        return estadisticas;
    }

    @Override
    public EstadisticasProyectoDto obtenerEstadisticasProyecto(Long idProyecto) {
        EstadisticasProyectoDto estadisticas = new EstadisticasProyectoDto();

        ProyectoInmobiliario proyecto = proyectoRepository.findById(idProyecto).orElse(null);
        if (proyecto != null) {
            estadisticas.setProyecto(modelMapper.map(proyecto, ProyectoInmobiliarioDto.class));

            List<UnidadInmobiliaria> unidades = unidadInmobiliariaRepository.findAll().stream()
                    .filter(u -> u.getProyecto() != null && u.getProyecto().getId_proyecto().equals(idProyecto))
                    .collect(Collectors.toList());

            estadisticas.setTotalUnidades(unidades.size());
            estadisticas.setUnidadesVendidas((int) unidades.stream()
                    .filter(u -> "vendido".equals(u.getEstado())).count());
            estadisticas.setUnidadesDisponibles((int) unidades.stream()
                    .filter(u -> "disponible".equals(u.getEstado())).count());

            estadisticas.setValorTotalProyecto(unidades.stream()
                    .mapToDouble(UnidadInmobiliaria::getPrecio_venta)
                    .sum());

            estadisticas.setValorVendido(unidades.stream()
                    .filter(u -> "vendido".equals(u.getEstado()))
                    .mapToDouble(UnidadInmobiliaria::getPrecio_venta)
                    .sum());

            estadisticas.setValorDisponible(unidades.stream()
                    .filter(u -> "disponible".equals(u.getEstado()))
                    .mapToDouble(UnidadInmobiliaria::getPrecio_venta)
                    .sum());

            List<Credito> creditosAsociados = creditoRepository.findAll().stream()
                    .filter(c -> c.getUnidad() != null &&
                            c.getUnidad().getProyecto() != null &&
                            c.getUnidad().getProyecto().getId_proyecto().equals(idProyecto))
                    .collect(Collectors.toList());

            estadisticas.setCreditosAsociados(creditosAsociados.size());
            estadisticas.setMontoFinanciado(creditosAsociados.stream()
                    .mapToDouble(Credito::getMonto_principal)
                    .sum());

            if (unidades.size() > 0) {
                estadisticas.setTasaVenta((estadisticas.getUnidadesVendidas() * 100.0) / unidades.size());
            }

            estadisticas.setUnidadesMasVendidas(unidades.stream()
                    .filter(u -> "vendido".equals(u.getEstado()))
                    .sorted((u1, u2) -> Double.compare(u2.getPrecio_venta(), u1.getPrecio_venta()))
                    .limit(5)
                    .map(u -> modelMapper.map(u, UnidadInmobiliariaDto.class))
                    .collect(Collectors.toList()));
        }

        return estadisticas;
    }

    @Override
    public byte[] exportarReporteCreditosExcel(LocalDate fechaInicio, LocalDate fechaFin) {
        return new byte[0];
    }

    @Override
    public byte[] exportarEstadoCuentaPdf(Long idCredito) {
        return new byte[0];
    }

    @Override
    public byte[] exportarCronogramaPdf(Long idCredito) {
        return new byte[0];
    }
}