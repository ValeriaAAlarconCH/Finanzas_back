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
public class Credito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_credito;

    private Integer meses_gracia;

    private Double monto_principal;

    private Double tasa_anual;

    private Integer plazo_meses;

    private LocalDate fecha_desembolso;

    private String numero_contrato;

    private String estado;

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_unidad")
    private UnidadInmobiliaria unidad;

    @ManyToOne
    @JoinColumn(name = "id_entidad")
    private EntidadFinanciera entidad;

    @ManyToOne
    @JoinColumn(name = "id_moneda")
    private Moneda moneda;

    @ManyToOne
    @JoinColumn(name = "id_tasa")
    private TipoTasaInteres tasa;

    @ManyToOne
    @JoinColumn(name = "id_capitalizacion")
    private Capitalizacion capitalizacion;

    @ManyToOne
    @JoinColumn(name = "id_gracia")
    private PeriodoGracia gracia;
}
