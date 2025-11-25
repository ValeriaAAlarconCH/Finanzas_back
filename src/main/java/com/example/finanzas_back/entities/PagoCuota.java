package com.example.finanzas_back.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(PagoCuota.PagoCuotaId.class)
public class PagoCuota {
    @Id
    @Column(name = "id_pago")
    private Long idPago;

    @Id
    @Column(name = "id_cuota")
    private Long idCuota;

    @ManyToOne
    @JoinColumn(name = "id_pago", insertable = false, updatable = false)
    private Pago pago;

    @ManyToOne
    @JoinColumn(name = "id_cuota", insertable = false, updatable = false)
    private Cuota cuota;

    private Double monto_aplicado;

    private LocalDate fecha_pago;

    @lombok.Data
    public static class PagoCuotaId implements java.io.Serializable {
        private Long idPago;
        private Long idCuota;
    }
}
