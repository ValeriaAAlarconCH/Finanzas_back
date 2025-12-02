package com.example.finanzas_back.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // ⬅️ ¡No olvides esta importación!
import org.springframework.web.bind.annotation.*;
import com.example.finanzas_back.dtos.ConfiguracionDto;
import com.example.finanzas_back.interfaces.IConfiguracionService;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:4200",
        allowCredentials = "true",
        exposedHeaders = "Authorization")

@RequestMapping("/configuraciones")
public class ConfiguracionController {
    @Autowired
    private IConfiguracionService configuracionservice;

    /**
     * Permite registrar una nueva configuración del sistema.
     * Acceso: Exclusivo para ADMIN (operación crítica).
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/registrar")
    public ResponseEntity<ConfiguracionDto> configuracion(@RequestBody ConfiguracionDto configuraciondto) {
        return ResponseEntity.ok(configuracionservice.grabarConfiguracion(configuraciondto));
    }

    /**
     * Permite listar todas las configuraciones.
     * Acceso: ADMIN y ANALISTA (si necesitan ver los parámetros del sistema, de lo contrario, solo ADMIN).
     * Nota: Si solo el ADMIN debe ver, usa: @PreAuthorize("hasRole('ADMIN')")
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ANALISTA')")
    @GetMapping("/listar")
    public ResponseEntity<List<ConfiguracionDto>> getConfiguraciones() {
        return ResponseEntity.ok(configuracionservice.getConfiguraciones());
    }

    /**
     * Permite eliminar una configuración.
     * Acceso: Exclusivo para ADMIN (operación muy destructiva).
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable("id") Long id) {
        configuracionservice.eliminar(id);
        return ResponseEntity.ok("Configuración eliminada correctamente");
    }

    /**
     * Permite actualizar una configuración existente.
     * Acceso: Exclusivo para ADMIN.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/actualizar")
    public ResponseEntity<ConfiguracionDto> actualizar(@RequestBody ConfiguracionDto configuraciondto) {
        ConfiguracionDto actualizado = configuracionservice.actualizar(configuraciondto);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * Permite obtener una configuración específica por su ID.
     * Acceso: ADMIN, ANALISTA y USER (asumiendo que podría ser una configuración de vista general).
     * Nota: Si solo el ADMIN debe ver, usa: @PreAuthorize("hasRole('ADMIN')")
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ANALISTA', 'USER')")
    @GetMapping("/listarid/{id}")
    public ResponseEntity<ConfiguracionDto> obtenerPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(configuracionservice.obtenerPorId(id));
    }
}