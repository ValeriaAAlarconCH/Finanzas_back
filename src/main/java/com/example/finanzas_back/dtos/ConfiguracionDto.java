package com.example.finanzas_back.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionDto implements Serializable {
    private Long id_config;
    private Integer convencion_dias;
    private String periodicidad;

    private ClienteDto clientedto;
    private MonedaDto monedadto;
    private TipoTasaInteresDto tasadto;
    private CapitalizacionDto capitalizaciondto;
}
