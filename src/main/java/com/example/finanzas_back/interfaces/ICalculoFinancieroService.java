package com.example.finanzas_back.services;

import com.example.finanzas_back.dtos.CapitalizacionDto;
import com.example.finanzas_back.dtos.CreditoDto;
import com.example.finanzas_back.dtos.CuotaDto;
import com.example.finanzas_back.dtos.IndicadorFinancieroDto;
import com.example.finanzas_back.interfaces.ICalculoFinancieroService;
import com.example.finanzas_back.repositories.CapitalizacionRepository;
import com.example.finanzas_back.repositories.PeriodoGraciaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalculoFinancieroService implements ICalculoFinancieroService {

    @Autowired
    private CapitalizacionRepository capitalizacionRepository;

    @Autowired
    private PeriodoGraciaRepository periodoGraciaRepository;

    @Autowired
    private ModelMapper modelMapper;

    private static final Double SEGURO_DESGRAVAMEN_PORCENTAJE = 0.00035; // 0.035% mensual
    private static final Double SEGURO_INMUEBLE_PORCENTAJE = 0.00025;    // 0.025% mensual
    private static final Integer DIAS_POR_MES = 30;

    @Override
    public List<CuotaDto> generarCronogramaFrances(CreditoDto creditoDto) {
        if (creditoDto == null) {
            throw new RuntimeException("El crédito no puede ser nulo");
        }

        Double monto = creditoDto.getMonto_principal();
        Double tasaAnual = creditoDto.getTasa_anual();
        Integer plazo = creditoDto.getPlazo_meses();
        Integer mesesGracia = creditoDto.getMeses_gracia() != null ? creditoDto.getMeses_gracia() : 0;

        String tipoGracia = "parcial"; // valor por defecto
        if (creditoDto.getGraciadto() != null && creditoDto.getGraciadto().getTipo() != null) {
            tipoGracia = creditoDto.getGraciadto().getTipo();
        }

        Double tasaPeriodica = calcularTasaPeriodica(
                tasaAnual,
                creditoDto.getTasadto() != null ? creditoDto.getTasadto().getTipo() : "efectiva",
                creditoDto.getCapitalizaciondto()
        );

        Double cuotaConstante = calcularCuotaConstante(monto, tasaPeriodica, plazo - mesesGracia);

        List<CuotaDto> cronograma = new ArrayList<>();
        Double saldo = monto;
        LocalDate fechaCuota = creditoDto.getFecha_desembolso() != null ?
                creditoDto.getFecha_desembolso().plusMonths(1) : LocalDate.now().plusMonths(1);

        for (int i = 1; i <= plazo; i++) {
            CuotaDto cuota = new CuotaDto();
            cuota.setNumero_cuota(i);
            cuota.setFecha_vencimiento(fechaCuota);
            cuota.setDias_periodo(DIAS_POR_MES);
            cuota.setSaldo_inicial(saldo);

            Double interes = saldo * tasaPeriodica;
            cuota.setInteres_programado(interes);

            Double amortizacion;
            if (i <= mesesGracia && "parcial".equals(tipoGracia)) {
                amortizacion = 0.0;
            } else if (i <= mesesGracia && "total".equals(tipoGracia)) {
                amortizacion = 0.0;
                interes = 0.0;
                cuota.setInteres_programado(0.0);
            } else {
                int periodoPago = i - mesesGracia;
                if (periodoPago <= 0) periodoPago = 1;

                amortizacion = cuotaConstante - interes;
                if (amortizacion > saldo) {
                    amortizacion = saldo;
                }
            }

            cuota.setCapital_programado(amortizacion);

            Double seguroDesgravamen = calcularSeguroDesgravamen(saldo, SEGURO_DESGRAVAMEN_PORCENTAJE);
            Double seguroInmueble = calcularSeguroInmueble(saldo, SEGURO_INMUEBLE_PORCENTAJE);
            Double otrosCargos = seguroDesgravamen + seguroInmueble;
            cuota.setOtros_cargos(otrosCargos);

            Double totalCuota = amortizacion + interes + otrosCargos;

            if (i <= mesesGracia && "total".equals(tipoGracia)) {
                totalCuota = 0.0;
            }

            cuota.setTotal_cuota(totalCuota);

            saldo -= amortizacion;
            if (saldo < 0) saldo = 0.0;
            cuota.setSaldo_final(saldo);

            cuota.setEstado("pendiente");
            cuota.setCreditodto(creditoDto);

            cronograma.add(cuota);
            fechaCuota = fechaCuota.plusMonths(1);
        }

        return cronograma;
    }

    @Override
    public Double calcularTasaPeriodica(Double tasaAnual, String tipoTasa, CapitalizacionDto capitalizacionDto) {
        if (tasaAnual == null) {
            throw new RuntimeException("La tasa anual no puede ser nula");
        }

        if (capitalizacionDto == null || capitalizacionDto.getPeriodos_por_ano() == null) {
            throw new RuntimeException("La capitalización no puede ser nula");
        }

        Double tasaDecimal = tasaAnual / 100.0;
        Double periodosPorAno = capitalizacionDto.getPeriodos_por_ano();

        if ("efectiva".equalsIgnoreCase(tipoTasa)) {
            // Tasa Efectiva Anual (TEA) a Tasa Efectiva Periódica (TEP)
            // TEP = (1 + TEA)^(1/m) - 1
            return Math.pow(1 + tasaDecimal, 1.0 / periodosPorAno) - 1;
        } else {
            // Tasa Nominal Anual (TNA) a Tasa Periódica
            // Tasa Periódica = TNA / m
            return tasaDecimal / periodosPorAno;
        }
    }

    @Override
    public Double calcularCuotaConstante(Double monto, Double tasaPeriodica, Integer plazo) {
        if (monto == null || monto <= 0) {
            throw new RuntimeException("El monto debe ser mayor a cero");
        }

        if (plazo == null || plazo <= 0) {
            throw new RuntimeException("El plazo debe ser mayor a cero");
        }

        if (tasaPeriodica == 0) {
            return monto / plazo;
        }

        // Fórmula del metodo francés: Cuota = P * [i(1+i)^n] / [(1+i)^n - 1]
        Double factor = Math.pow(1 + tasaPeriodica, plazo);
        return monto * (tasaPeriodica * factor) / (factor - 1);
    }

    @Override
    public IndicadorFinancieroDto calcularIndicadoresFinancieros(CreditoDto creditoDto, List<CuotaDto> cronograma) {
        IndicadorFinancieroDto indicador = new IndicadorFinancieroDto();
        indicador.setFecha_calculo(LocalDate.now());

        if (creditoDto == null || cronograma == null || cronograma.isEmpty()) {
            throw new RuntimeException("Datos insuficientes para calcular indicadores");
        }

        List<Double> flujos = new ArrayList<>();
        flujos.add(-creditoDto.getMonto_principal()); // Desembolso inicial (negativo)

        for (CuotaDto cuota : cronograma) {
            flujos.add(cuota.getTotal_cuota());
        }

        List<CuotaDto> flujosParaCalculo = new ArrayList<>();
        for (int i = 0; i < flujos.size(); i++) {
            CuotaDto cuotaFlujo = new CuotaDto();
            cuotaFlujo.setNumero_cuota(i);
            cuotaFlujo.setTotal_cuota(flujos.get(i));
            flujosParaCalculo.add(cuotaFlujo);
        }

        Double tir = calcularTIR(flujosParaCalculo);
        indicador.setTIR(tir);

        Double tasaDescuento = calcularTasaPeriodica(
                creditoDto.getTasa_anual(),
                creditoDto.getTasadto() != null ? creditoDto.getTasadto().getTipo() : "efectiva",
                creditoDto.getCapitalizaciondto()
        );
        Double van = calcularVAN(flujosParaCalculo, tasaDescuento);
        indicador.setVAN(van);

        Double periodosPorAno = creditoDto.getCapitalizaciondto() != null ?
                creditoDto.getCapitalizaciondto().getPeriodos_por_ano() : 12.0;
        Double tcea = calcularTCEA(tir, periodosPorAno.intValue());
        Double trea = calcularTREA(tir, periodosPorAno.intValue());
        indicador.setTCEA(tcea);
        indicador.setTREA(trea);

        Double duracion = calcularDuracion(cronograma, tasaDescuento);
        Double duracionModificada = calcularDuracionModificada(duracion, tasaDescuento);
        Double convexidad = calcularConvexidad(cronograma, tasaDescuento);

        indicador.setDuracion(duracion);
        indicador.setDuracion_modificada(duracionModificada);
        indicador.setConvexidad(convexidad);
        indicador.setCreditodto(creditoDto);

        return indicador;
    }

    @Override
    public Double calcularVAN(List<CuotaDto> flujos, Double tasaDescuento) {
        if (flujos == null || flujos.isEmpty()) {
            return 0.0;
        }

        Double van = 0.0;

        for (int i = 0; i < flujos.size(); i++) {
            CuotaDto cuota = flujos.get(i);
            if (cuota.getTotal_cuota() != null) {
                Double flujo = cuota.getTotal_cuota();
                van += flujo / Math.pow(1 + tasaDescuento, i);
            }
        }

        return van;
    }

    @Override
    public Double calcularTIR(List<CuotaDto> flujos) {
        if (flujos == null || flujos.isEmpty()) {
            return 0.0;
        }

        Double tasaBaja = 0.0;
        Double tasaAlta = 1.0; // 100%
        Double precision = 0.0001; // 0.01%
        Double vanBaja = calcularVAN(flujos, tasaBaja);
        Double vanAlta = calcularVAN(flujos, tasaAlta);

        int intentos = 0;
        while (vanBaja * vanAlta > 0 && intentos < 10) {
            if (vanBaja > 0) {
                tasaAlta *= 2;
            } else {
                tasaBaja /= 2;
            }
            vanBaja = calcularVAN(flujos, tasaBaja);
            vanAlta = calcularVAN(flujos, tasaAlta);
            intentos++;
        }

        Double tasaMedia = (tasaBaja + tasaAlta) / 2;
        Double vanMedia = calcularVAN(flujos, tasaMedia);

        while (Math.abs(vanMedia) > precision && (tasaAlta - tasaBaja) > precision) {
            if (vanBaja * vanMedia < 0) {
                tasaAlta = tasaMedia;
                vanAlta = vanMedia;
            } else {
                tasaBaja = tasaMedia;
                vanBaja = vanMedia;
            }

            tasaMedia = (tasaBaja + tasaAlta) / 2;
            vanMedia = calcularVAN(flujos, tasaMedia);
        }

        return tasaMedia * 100;
    }

    @Override
    public Double calcularTCEA(Double tir, Integer periodosPorAno) {
        if (tir == null || periodosPorAno == null) {
            return 0.0;
        }

        // TCEA = (1 + TIR)^periodosPorAno - 1
        Double tirDecimal = tir / 100.0;
        return (Math.pow(1 + tirDecimal, periodosPorAno) - 1) * 100;
    }

    @Override
    public Double calcularTREA(Double tir, Integer periodosPorAno) {
        return calcularTCEA(tir, periodosPorAno);
    }

    @Override
    public Double calcularDuracion(List<CuotaDto> cronograma, Double tasaDescuento) {
        if (cronograma == null || cronograma.isEmpty()) {
            return 0.0;
        }

        Double valorPresenteTotal = 0.0;
        Double duracionNumerador = 0.0;

        for (CuotaDto cuota : cronograma) {
            if (cuota.getTotal_cuota() != null && cuota.getNumero_cuota() != null) {
                Double valorPresente = cuota.getTotal_cuota() / Math.pow(1 + tasaDescuento, cuota.getNumero_cuota());
                valorPresenteTotal += valorPresente;
                duracionNumerador += cuota.getNumero_cuota() * valorPresente;
            }
        }

        if (valorPresenteTotal == 0) {
            return 0.0;
        }

        return duracionNumerador / valorPresenteTotal;
    }

    @Override
    public Double calcularDuracionModificada(Double duracion, Double tasaDescuento) {
        if (duracion == null || tasaDescuento == null) {
            return 0.0;
        }

        return duracion / (1 + tasaDescuento);
    }

    @Override
    public Double calcularConvexidad(List<CuotaDto> cronograma, Double tasaDescuento) {
        if (cronograma == null || cronograma.isEmpty()) {
            return 0.0;
        }

        Double valorPresenteTotal = 0.0;
        Double convexidadNumerador = 0.0;

        for (CuotaDto cuota : cronograma) {
            if (cuota.getTotal_cuota() != null && cuota.getNumero_cuota() != null) {
                Double valorPresente = cuota.getTotal_cuota() / Math.pow(1 + tasaDescuento, cuota.getNumero_cuota());
                valorPresenteTotal += valorPresente;
                convexidadNumerador += cuota.getNumero_cuota() * (cuota.getNumero_cuota() + 1) * valorPresente;
            }
        }

        if (valorPresenteTotal == 0) {
            return 0.0;
        }

        return convexidadNumerador / (valorPresenteTotal * Math.pow(1 + tasaDescuento, 2));
    }

    @Override
    public List<CuotaDto> aplicarPeriodoGracia(List<CuotaDto> cronograma, Integer mesesGracia, String tipoGracia) {
        if (cronograma == null || cronograma.isEmpty()) {
            return new ArrayList<>();
        }

        if (mesesGracia == null || mesesGracia == 0 || tipoGracia == null) {
            return cronograma;
        }

        List<CuotaDto> cronogramaConGracia = new ArrayList<>();

        for (int i = 0; i < cronograma.size(); i++) {
            CuotaDto cuota = cronograma.get(i);
            CuotaDto cuotaModificada = modelMapper.map(cuota, CuotaDto.class);

            if (i < mesesGracia) {
                if ("total".equalsIgnoreCase(tipoGracia)) {
                    cuotaModificada.setCapital_programado(0.0);
                    cuotaModificada.setInteres_programado(0.0);
                    cuotaModificada.setOtros_cargos(0.0);
                    cuotaModificada.setTotal_cuota(0.0);
                } else {
                    cuotaModificada.setCapital_programado(0.0);
                    cuotaModificada.setTotal_cuota(
                            cuotaModificada.getInteres_programado() +
                                    cuotaModificada.getOtros_cargos()
                    );
                }
            }

            cronogramaConGracia.add(cuotaModificada);
        }

        return cronogramaConGracia;
    }

    @Override
    public Double calcularSeguroDesgravamen(Double saldo, Double porcentaje) {
        if (saldo == null || saldo <= 0) {
            return 0.0;
        }

        if (porcentaje == null) {
            porcentaje = SEGURO_DESGRAVAMEN_PORCENTAJE;
        }

        return saldo * porcentaje;
    }

    @Override
    public Double calcularSeguroInmueble(Double saldo, Double porcentaje) {
        if (saldo == null || saldo <= 0) {
            return 0.0;
        }

        if (porcentaje == null) {
            porcentaje = SEGURO_INMUEBLE_PORCENTAJE;
        }

        return saldo * porcentaje;
    }
}
