package com.example.finanzas_back.repositories;

import com.example.finanzas_back.entities.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    @Query("SELECT p FROM Pago p WHERE p.credito.id_credito = :idCredito ORDER BY p.fecha_pago DESC")
    List<Pago> findByCreditoId(@Param("idCredito") Long idCredito);

    @Query("SELECT p FROM Pago p WHERE p.cliente.id_cliente = :idCliente ORDER BY p.fecha_pago DESC")
    List<Pago> findByClienteId(@Param("idCliente") Long idCliente);

    @Query("SELECT SUM(p.monto) FROM Pago p WHERE p.credito.id_credito = :idCredito")
    Double sumMontoByCreditoId(@Param("idCredito") Long idCredito);

    @Query("SELECT p FROM Pago p WHERE p.fecha_pago BETWEEN :fechaInicio AND :fechaFin")
    List<Pago> findByFechaBetween(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    // MÉTODO FALTANTE: findByCreditoIdAndFechaBetween
    @Query("SELECT p FROM Pago p WHERE p.credito.id_credito = :idCredito " +
            "AND p.fecha_pago BETWEEN :fechaInicio AND :fechaFin " +
            "ORDER BY p.fecha_pago DESC")
    List<Pago> findByCreditoIdAndFechaBetween(
            @Param("idCredito") Long idCredito,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);
}
