package com.example.finanzas_back.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; //
import org.springframework.web.bind.annotation.*;
import com.example.finanzas_back.dtos.IndicadorFinancieroDto;
import com.example.finanzas_back.interfaces.IIndicadorFinancieroService;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:4200",
        allowCredentials = "true",
        exposedHeaders = "Authorization")

@RequestMapping("/indicadoresfinancieros")
public class IndicadorFinancieroController {
    @Autowired
    private IIndicadorFinancieroService indicadorservice;

    /**
     * Permite registrar un nuevo indicador financiero (Ej. una nueva métrica).
     * Acceso: Solo para ADMIN.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/registrar")
    public ResponseEntity<IndicadorFinancieroDto> indicadorfinanciero(@RequestBody IndicadorFinancieroDto indicadordto) {
        return ResponseEntity.ok(indicadorservice.grabarIndicadorFinanciero(indicadordto));
    }

    /**
     * Permite listar todos los indicadores financieros disponibles.
     * Acceso: ADMIN y ANALISTA (El Analista necesita acceder a las métricas).
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ANALISTA')")
    @GetMapping("/listar")
    public ResponseEntity<List<IndicadorFinancieroDto>> getIndicadoresFinancieros() {
        return ResponseEntity.ok(indicadorservice.getIndicadoresFinancieros());
    }

    /**
     * Permite eliminar un indicador financiero.
     * Acceso: Solo para ADMIN (operación crítica de gestión de datos).
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable("id") Long id) {
        indicadorservice.eliminar(id);
        return ResponseEntity.ok("Indicador Financiero eliminado correctamente");
    }

    /**
     * Permite actualizar un indicador financiero existente.
     * Acceso: Solo para ADMIN.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/actualizar")
    public ResponseEntity<IndicadorFinancieroDto> actualizar(@RequestBody IndicadorFinancieroDto indicadordto) {
        IndicadorFinancieroDto actualizado = indicadorservice.actualizar(indicadordto);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * Permite obtener un indicador financiero por su ID.
     * Acceso: ADMIN, ANALISTA y USER (el USER podría necesitar ver el valor de un indicador específico).
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ANALISTA', 'USER')")
    @GetMapping("/listarid/{id}")
    public ResponseEntity<IndicadorFinancieroDto> obtenerPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(indicadorservice.obtenerPorId(id));
    }
}