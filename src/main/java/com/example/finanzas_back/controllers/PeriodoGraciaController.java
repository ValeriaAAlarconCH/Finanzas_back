package com.example.finanzas_back.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.finanzas_back.dtos.PeriodoGraciaDto;
import com.example.finanzas_back.interfaces.IPeriodoGraciaService;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:4200",
        allowCredentials = "true",
        exposedHeaders = "Authorization")

@RequestMapping("/periodosgracia")
public class PeriodoGraciaController {
    @Autowired
    private IPeriodoGraciaService graciaservice;

    @PostMapping("/registrar")
    public ResponseEntity<PeriodoGraciaDto> periodogracia(@RequestBody PeriodoGraciaDto graciadto) {
        return ResponseEntity.ok(graciaservice.grabarPeriodoGracia(graciadto));
    }

    @GetMapping("/listar")
    public ResponseEntity<List<PeriodoGraciaDto>> getPeriodosGracia() {
        return ResponseEntity.ok(graciaservice.getPeriodosGracia());
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable("id") Long id) {
        graciaservice.eliminar(id);
        return ResponseEntity.ok("Periodo de Gracia eliminado correctamente");
    }

    @PutMapping("/actualizar")
    public ResponseEntity<PeriodoGraciaDto> actualizar(@RequestBody PeriodoGraciaDto graciadto) {
        PeriodoGraciaDto actualizado = graciaservice.actualizar(graciadto);
        return ResponseEntity.ok(actualizado);
    }

    @GetMapping("/listarid/{id}")
    public ResponseEntity<PeriodoGraciaDto> obtenerPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(graciaservice.obtenerPorId(id));
    }
}
