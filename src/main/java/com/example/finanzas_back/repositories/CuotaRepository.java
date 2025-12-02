package com.example.finanzas_back.repositories;

import com.example.finanzas_back.entities.Cuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CuotaRepository extends JpaRepository<Cuota, Long> {
    @Query("SELECT c FROM Cuota c WHERE c.credito.id_credito = :idCredito ORDER BY c.numero_cuota")
    List<Cuota> findByCreditoId(@Param("idCredito") Long idCredito);

    @Query("SELECT c FROM Cuota c WHERE c.credito.id_credito = :idCredito AND c.estado = :estado")
    List<Cuota> findByCreditoIdAndEstado(@Param("idCredito") Long idCredito, @Param("estado") String estado);

    @Query("SELECT c FROM Cuota c WHERE c.fecha_vencimiento BETWEEN :fechaInicio AND :fechaFin")
    List<Cuota> findByFechaVencimientoBetween(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    // ESTE MÉTODO SÍ SE USA en ReporteService.calcularCuotasVencidasUltimoMes()
    @Query("SELECT SUM(c.total_cuota) FROM Cuota c WHERE c.credito.id_credito = :idCredito AND c.estado = 'pendiente'")
    Double sumTotalPendienteByCredito(@Param("idCredito") Long idCredito);

    // ESTE MÉTODO SÍ SE USA en ReporteService (implícitamente)
    @Query("SELECT c FROM Cuota c WHERE c.credito.id_credito = :idCredito AND c.numero_cuota = :numeroCuota")
    Cuota findByCreditoIdAndNumeroCuota(@Param("idCredito") Long idCredito, @Param("numeroCuota") Integer numeroCuota);
}
