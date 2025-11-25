package com.example.finanzas_back.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/registrar")
    public ResponseEntity<ConfiguracionDto> configuracion(@RequestBody ConfiguracionDto configuraciondto) {
        return ResponseEntity.ok(configuracionservice.grabarConfiguracion(configuraciondto));
    }

    @GetMapping("/listar")
    public ResponseEntity<List<ConfiguracionDto>> getConfiguraciones() {
        return ResponseEntity.ok(configuracionservice.getConfiguraciones());
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable("id") Long id) {
        configuracionservice.eliminar(id);
        return ResponseEntity.ok("Configuración eliminada correctamente");
    }

    @PutMapping("/actualizar")
    public ResponseEntity<ConfiguracionDto> actualizar(@RequestBody ConfiguracionDto configuraciondto) {
        ConfiguracionDto actualizado = configuracionservice.actualizar(configuraciondto);
        return ResponseEntity.ok(actualizado);
    }

    @GetMapping("/listarid/{id}")
    public ResponseEntity<ConfiguracionDto> obtenerPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(configuracionservice.obtenerPorId(id));
    }
}
