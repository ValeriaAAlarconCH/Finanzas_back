package com.example.finanzas_back.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Importación necesaria
import org.springframework.web.bind.annotation.*;
import com.example.finanzas_back.dtos.CapitalizacionDto;
import com.example.finanzas_back.interfaces.ICapitalizacionService;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:4200",
        allowCredentials = "true",
        exposedHeaders = "Authorization")

@RequestMapping("/capitalizaciones")
public class CapitalizacionController {
    @Autowired
    private ICapitalizacionService capitalizacionservice;

    /**
     * Permite registrar una nueva capitalización.
     * Acceso: Solo para ADMIN.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/registrar")
    public ResponseEntity<CapitalizacionDto> capitalizacion(@RequestBody CapitalizacionDto capitalizaciondto) {
        return ResponseEntity.ok(capitalizacionservice.grabarCapitalizacion(capitalizaciondto));
    }

    /**
     * Permite listar todas las capitalizaciones.
     * Acceso: ADMIN y ANALISTA (ambos suelen necesitar ver datos).
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ANALISTA')")
    @GetMapping("/listar")
    public ResponseEntity<List<CapitalizacionDto>> getCapitalizaciones() {
        return ResponseEntity.ok(capitalizacionservice.getCapitalizaciones());
    }

    /**
     * Permite eliminar una capitalización.
     * Acceso: Solo para ADMIN (operación destructiva).
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable("id") Long id) {
        capitalizacionservice.eliminar(id);
        return ResponseEntity.ok("Capitalización eliminada correctamente");
    }

    /**
     * Permite actualizar una capitalización existente.
     * Acceso: Solo para ADMIN.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/actualizar")
    public ResponseEntity<CapitalizacionDto> actualizar(@RequestBody CapitalizacionDto capitalizaciondto) {
        CapitalizacionDto actualizado = capitalizacionservice.actualizar(capitalizaciondto);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * Permite obtener una capitalización por su ID.
     * Acceso: ADMIN, ANALISTA y USER (asumo que el USER necesita ver sus propios datos).
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ANALISTA', 'USER')")
    @GetMapping("/listarid/{id}")
    public ResponseEntity<CapitalizacionDto> obtenerPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(capitalizacionservice.obtenerPorId(id));
    }
}