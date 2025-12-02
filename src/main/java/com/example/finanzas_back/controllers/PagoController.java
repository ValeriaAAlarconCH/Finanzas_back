package com.example.finanzas_back.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; //
import org.springframework.web.bind.annotation.*;
import com.example.finanzas_back.dtos.PagoDto;
import com.example.finanzas_back.interfaces.IPagoService;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:4200",
        allowCredentials = "true",
        exposedHeaders = "Authorization")

@RequestMapping("/pagos")
public class PagoController {
    @Autowired
    private IPagoService pagoservice;

    /**
     * Permite registrar un nuevo pago.
     * Acceso: Solo para ADMIN (o un sistema interno con permisos de ADMIN).
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/registrar")
    public ResponseEntity<PagoDto> pago(@RequestBody PagoDto pagodto) {
        return ResponseEntity.ok(pagoservice.grabarPago(pagodto));
    }

    /**
     * Permite listar todos los pagos registrados.
     * Acceso: ADMIN y ANALISTA (para fines de auditoría y análisis).
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ANALISTA')")
    @GetMapping("/listar")
    public ResponseEntity<List<PagoDto>> getPagos() {
        return ResponseEntity.ok(pagoservice.getPagos());
    }

    /**
     * Permite eliminar un registro de pago (ej. por reversión o error).
     * Acceso: Solo para ADMIN (operación de alto riesgo).
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable("id") Long id) {
        pagoservice.eliminar(id);
        return ResponseEntity.ok("Pago eliminado correctamente");
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/actualizar")
    public ResponseEntity<PagoDto> actualizar(@RequestBody PagoDto pagodto) {
        PagoDto actualizado = pagoservice.actualizar(pagodto);
        return ResponseEntity.ok(actualizado);
    }


    @PreAuthorize("hasAnyAuthority('ADMIN', 'ANALISTA', 'USER')")
    @GetMapping("/listarid/{id}")
    public ResponseEntity<PagoDto> obtenerPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(pagoservice.obtenerPorId(id));
    }
}