package com.example.finanzas_back.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; //  Importación necesaria
import org.springframework.web.bind.annotation.*;
import com.example.finanzas_back.dtos.CuotaDto;
import com.example.finanzas_back.interfaces.ICuotaService;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:4200",
        allowCredentials = "true",
        exposedHeaders = "Authorization")

@RequestMapping("/cuotas")
public class CuotaController {
    @Autowired
    private ICuotaService cuotaservice;

    /**
     * Permite registrar o marcar el pago de una cuota.
     * Acceso: Solo para ADMIN.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/registrar")
    public ResponseEntity<CuotaDto> cuota(@RequestBody CuotaDto cuotadto) {
        return ResponseEntity.ok(cuotaservice.grabarCuota(cuotadto));
    }

    /**
     * Permite listar todas las cuotas (histórico de pagos).
     * Acceso: ADMIN y ANALISTA (necesitan ver el estado de todos los pagos).
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ANALISTA')")
    @GetMapping("/listar")
    public ResponseEntity<List<CuotaDto>> getCuotas() {
        return ResponseEntity.ok(cuotaservice.getCuotas());
    }

    /**
     * Permite eliminar una cuota (ej. por error en el registro).
     * Acceso: Solo para ADMIN (operación de alta sensibilidad).
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable("id") Long id) {
        cuotaservice.eliminar(id);
        return ResponseEntity.ok("Cuota eliminada correctamente");
    }

    /**
     * Permite actualizar una cuota (ej. corregir monto o estado de pago).
     * Acceso: Solo para ADMIN.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/actualizar")
    public ResponseEntity<CuotaDto> actualizar(@RequestBody CuotaDto cuotadto) {
        CuotaDto actualizado = cuotaservice.actualizar(cuotadto);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * Permite obtener una cuota específica por su ID.
     * Acceso: ADMIN, ANALISTA y USER (el USER necesita ver el estado de sus cuotas).
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ANALISTA', 'USER')")
    @GetMapping("/listarid/{id}")
    public ResponseEntity<CuotaDto> obtenerPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(cuotaservice.obtenerPorId(id));
    }
}