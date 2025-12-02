package com.example.finanzas_back.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReporteMorosidadDto implements Serializable {
    private LocalDate fechaGeneracion;
    private Double indiceMorosidad;
    private Double carteraVencida;
    private Double carteraTotal;
    private Integer clientesMorosos;
    private Integer totalClientes;
    private Double tasaMorosidad;
    private List<ClienteMorosoDto> topClientesMorosos;
    private Map<String, Double> morosidadPorEntidad;
    private List<MorosidadMensualDto> evolucionMorosidad;
}
