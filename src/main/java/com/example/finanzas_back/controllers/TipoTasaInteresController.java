package com.example.finanzas_back.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.finanzas_back.dtos.TipoTasaInteresDto;
import com.example.finanzas_back.interfaces.ITipoTasaInteresService;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:4200",
        allowCredentials = "true",
        exposedHeaders = "Authorization")

@RequestMapping("/tipostasainteres")
public class TipoTasaInteresController {
    @Autowired
    private ITipoTasaInteresService tasaservice;

    @PostMapping("/registrar")
    public ResponseEntity<TipoTasaInteresDto> tipotasainteres(@RequestBody TipoTasaInteresDto tasadto) {
        return ResponseEntity.ok(tasaservice.grabarTipoTasaInteres(tasadto));
    }

    @GetMapping("/listar")
    public ResponseEntity<List<TipoTasaInteresDto>> getTiposTasaInteres() {
        return ResponseEntity.ok(tasaservice.getTiposTasaInteres());
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable("id") Long id) {
        tasaservice.eliminar(id);
        return ResponseEntity.ok("Tipo de Tasa de Interés eliminado correctamente");
    }

    @PutMapping("/actualizar")
    public ResponseEntity<TipoTasaInteresDto> actualizar(@RequestBody TipoTasaInteresDto tasadto) {
        TipoTasaInteresDto actualizado = tasaservice.actualizar(tasadto);
        return ResponseEntity.ok(actualizado);
    }

    @GetMapping("/listarid/{id}")
    public ResponseEntity<TipoTasaInteresDto> obtenerPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(tasaservice.obtenerPorId(id));
    }
}
