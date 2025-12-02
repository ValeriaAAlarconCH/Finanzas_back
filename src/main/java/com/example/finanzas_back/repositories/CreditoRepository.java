package com.example.finanzas_back.repositories;

import com.example.finanzas_back.entities.Credito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditoRepository extends JpaRepository<Credito, Long> {
    @Query("SELECT c FROM Credito c WHERE c.cliente.id_cliente = :idCliente")
    List<Credito> findByClienteId(@Param("idCliente") Long idCliente);

    @Query("SELECT c FROM Credito c WHERE c.estado = :estado")
    List<Credito> findByEstado(@Param("estado") String estado);

    // ESTE METODO SÍ SE USA en ValidacionService.validarHistorialCrediticio()
    @Query("SELECT c FROM Credito c WHERE c.numero_contrato = :numeroContrato")
    Credito findByNumeroContrato(@Param("numeroContrato") String numeroContrato);

    // ESTE METODO SÍ SE USA en ReporteService
    @Query("SELECT c FROM Credito c WHERE c.unidad.id_unidad = :idUnidad")
    List<Credito> findByUnidadId(@Param("idUnidad") Long idUnidad);

    // ESTE METODO SÍ SE USA en ReporteService.generarReporteCreditos()
    @Query("SELECT c FROM Credito c WHERE c.entidad.id_entidad = :idEntidad")
    List<Credito> findByEntidadId(@Param("idEntidad") Long idEntidad);

    // ESTE METODO SÍ SE USA en ReporteService.calcularKPIs()
    @Query("SELECT COUNT(c) FROM Credito c WHERE c.cliente.id_cliente = :idCliente AND c.estado = 'activo'")
    Long countCreditosActivosByCliente(@Param("idCliente") Long idCliente);
}
