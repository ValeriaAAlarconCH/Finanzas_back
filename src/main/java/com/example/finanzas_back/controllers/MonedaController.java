package com.example.finanzas_back.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/registrar")
    public ResponseEntity<MonedaDto> moneda(@RequestBody MonedaDto monedadto) {
        return ResponseEntity.ok(monedaservice.grabarMoneda(monedadto));
    }

    @GetMapping("/listar")
    public ResponseEntity<List<MonedaDto>> getMonedas() {
        return ResponseEntity.ok(monedaservice.getMonedas());
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable("id") Long id) {
        monedaservice.eliminar(id);
        return ResponseEntity.ok("Moneda eliminada correctamente");
    }

    @PutMapping("/actualizar")
    public ResponseEntity<MonedaDto> actualizar(@RequestBody MonedaDto monedadto) {
        MonedaDto actualizado = monedaservice.actualizar(monedadto);
        return ResponseEntity.ok(actualizado);
    }

    @GetMapping("/listarid/{id}")
    public ResponseEntity<MonedaDto> obtenerPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(monedaservice.obtenerPorId(id));
    }
}
