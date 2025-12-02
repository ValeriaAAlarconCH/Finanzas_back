package com.example.finanzas_back.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // ⬅️ Importación necesaria
import org.springframework.web.bind.annotation.*;
import com.example.finanzas_back.dtos.MonedaDto;
import com.example.finanzas_back.interfaces.IMonedaService;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:4200",
        allowCredentials = "true",
        exposedHeaders = "Authorization")

@RequestMapping("/monedas")
public class MonedaController {
    @Autowired
    private IMonedaService monedaservice;

    /**
     * Permite registrar una nueva moneda.
     * Acceso: Solo para ADMIN.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/registrar")
    public ResponseEntity<MonedaDto> moneda(@RequestBody MonedaDto monedadto) {
        return ResponseEntity.ok(monedaservice.grabarMoneda(monedadto));
    }

    /**
     * Permite listar todas las monedas disponibles.
     * Acceso: ADMIN, ANALISTA, y USER (Todos necesitan ver las opciones de moneda).
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ANALISTA', 'USER')")
    @GetMapping("/listar")
    public ResponseEntity<List<MonedaDto>> getMonedas() {
        return ResponseEntity.ok(monedaservice.getMonedas());
    }

    /**
     * Permite eliminar una moneda.
     * Acceso: Solo para ADMIN (operación de gestión de datos maestros).
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable("id") Long id) {
        monedaservice.eliminar(id);
        return ResponseEntity.ok("Moneda eliminada correctamente");
    }

    /**
     * Permite actualizar una moneda existente.
     * Acceso: Solo para ADMIN.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/actualizar")
    public ResponseEntity<MonedaDto> actualizar(@RequestBody MonedaDto monedadto) {
        MonedaDto actualizado = monedaservice.actualizar(monedadto);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * Permite obtener una moneda por su ID.
     * Acceso: ADMIN, ANALISTA, y USER.
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ANALISTA', 'USER')")
    @GetMapping("/listarid/{id}")
    public ResponseEntity<MonedaDto> obtenerPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(monedaservice.obtenerPorId(id));
    }
}