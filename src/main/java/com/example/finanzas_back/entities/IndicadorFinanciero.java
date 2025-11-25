package com.example.finanzas_back.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class IndicadorFinanciero {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_indicador;

    private LocalDate fecha_calculo;

    private Double VAN;

    private Double TIR;

    private Double TCEA;

    private Double TREA;

    private Double duracion;

    private Double duracion_modificada;

    private Double convexidad;

    @ManyToOne
    @JoinColumn(name = "id_credito")
    private Credito credito;
}
