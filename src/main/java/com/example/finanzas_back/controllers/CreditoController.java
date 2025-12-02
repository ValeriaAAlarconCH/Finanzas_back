package com.example.finanzas_back.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // ⬅️ ¡No olvides la importación!
import org.springframework.web.bind.annotation.*;
import com.example.finanzas_back.dtos.CreditoDto;
import com.example.finanzas_back.interfaces.ICreditoService;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:4200",
        allowCredentials = "true",
        exposedHeaders = "Authorization")

@RequestMapping("/creditos")
public class CreditoController {
    @Autowired
    private ICreditoService creditoservice;

    /**
     * Permite registrar un nuevo crédito.
     * Acceso: Solo para ADMIN.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/registrar")
    public ResponseEntity<CreditoDto> credito(@RequestBody CreditoDto creditodto) {
        return ResponseEntity.ok(creditoservice.grabarCredito(creditodto));
    }

    /**
     * Permite listar todos los créditos.
     * Acceso: ADMIN y ANALISTA (necesitan ver la cartera completa).
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ANALISTA')")
    @GetMapping("/listar")
    public ResponseEntity<List<CreditoDto>> getCreditos() {
        return ResponseEntity.ok(creditoservice.getCreditos());
    }

    /**
     * Permite eliminar un crédito.
     * Acceso: Solo para ADMIN (operación crítica).
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable("id") Long id) {
        creditoservice.eliminar(id);
        return ResponseEntity.ok("Crédito eliminado correctamente");
    }

    /**
     * Permite actualizar un crédito existente.
     * Acceso: Solo para ADMIN.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/actualizar")
    public ResponseEntity<CreditoDto> actualizar(@RequestBody CreditoDto creditodto) {
        CreditoDto actualizado = creditoservice.actualizar(creditodto);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * Permite obtener un crédito por su ID.
     * Acceso: ADMIN, ANALISTA y USER (asumo que el USER debe ver su propio crédito).
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ANALISTA', 'USER')")
    @GetMapping("/listarid/{id}")
    public ResponseEntity<CreditoDto> obtenerPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(creditoservice.obtenerPorId(id));
    }
}