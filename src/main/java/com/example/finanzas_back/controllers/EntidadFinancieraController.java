package com.example.finanzas_back.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.finanzas_back.dtos.EntidadFinancieraDto;
import com.example.finanzas_back.interfaces.IEntidadFinancieraService;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:4200",
        allowCredentials = "true",
        exposedHeaders = "Authorization")

@RequestMapping("/entidadesfinancieras")
public class EntidadFinancieraController {
    @Autowired
    private IEntidadFinancieraService entidadservice;

    @PostMapping("/registrar")
    public ResponseEntity<EntidadFinancieraDto> entidadfinanciera(@RequestBody EntidadFinancieraDto entidaddto) {
        return ResponseEntity.ok(entidadservice.grabarEntidadFinanciera(entidaddto));
    }

    @GetMapping("/listar")
    public ResponseEntity<List<EntidadFinancieraDto>> getEntidadesFinancieras() {
        return ResponseEntity.ok(entidadservice.getEntidadesFinancieras());
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable("id") Long id) {
        entidadservice.eliminar(id);
        return ResponseEntity.ok("Entidad Financiera eliminada correctamente");
    }

    @PutMapping("/actualizar")
    public ResponseEntity<EntidadFinancieraDto> actualizar(@RequestBody EntidadFinancieraDto entidaddto) {
        EntidadFinancieraDto actualizado = entidadservice.actualizar(entidaddto);
        return ResponseEntity.ok(actualizado);
    }

    @GetMapping("/listarid/{id}")
    public ResponseEntity<EntidadFinancieraDto> obtenerPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(entidadservice.obtenerPorId(id));
    }
}
