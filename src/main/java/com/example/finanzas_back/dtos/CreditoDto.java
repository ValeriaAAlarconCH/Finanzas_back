package com.example.finanzas_back.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreditoDto implements Serializable {
    private Long id_credito;
    private Integer meses_gracia;
    private Double monto_principal;
    private Double tasa_anual;
    private Integer plazo_meses;
    private LocalDate fecha_desembolso;
    private String numero_contrato;
    private String estado;

    private ClienteDto clientedto;
    private UnidadInmobiliariaDto unidaddto;
    private EntidadFinancieraDto entidaddto;
    private MonedaDto monedadto;
    private TipoTasaInteresDto tasadto;
    private CapitalizacionDto capitalizaciondto;
    private PeriodoGraciaDto graciadto;
}
