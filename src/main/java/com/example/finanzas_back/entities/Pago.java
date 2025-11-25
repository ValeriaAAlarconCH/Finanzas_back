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
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_pago;

    private LocalDate fecha_pago;

    private Double monto;

    private String metodo_pago;

    private String referencia;

    @ManyToOne
    @JoinColumn(name = "id_credito")
    private Credito credito;

    @ManyToOne
    @JoinColumn(name = "id_moneda")
    private Moneda moneda;

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

}
