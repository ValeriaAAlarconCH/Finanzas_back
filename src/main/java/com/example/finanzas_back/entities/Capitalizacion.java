package com.example.finanzas_back.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Capitalizacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_capitalizacion;

    private String nombre;

    private Double periodos_por_ano;
}
