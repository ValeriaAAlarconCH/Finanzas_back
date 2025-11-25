package com.example.finanzas_back.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Configuracion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_config;

    private Integer convencion_dias;

    private String periodicidad;

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_moneda")
    private Moneda moneda;

    @ManyToOne
    @JoinColumn(name = "id_tasa")
    private TipoTasaInteres tasa;

    @ManyToOne
    @JoinColumn(name = "id_capitalizacion")
    private Capitalizacion capitalizacion;
}
