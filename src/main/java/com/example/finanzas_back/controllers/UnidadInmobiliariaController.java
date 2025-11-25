package com.example.finanzas_back.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.finanzas_back.dtos.UnidadInmobiliariaDto;
import com.example.finanzas_back.interfaces.IUnidadInmobiliariaService;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:4200",
        allowCredentials = "true",
        exposedHeaders = "Authorization")

@RequestMapping("/unidadesinmobiliarias")
public class UnidadInmobiliariaController {
    @Autowired
    private IUnidadInmobiliariaService unidadservice;

    @PostMapping("/registrar")
    public ResponseEntity<UnidadInmobiliariaDto> unidadinmobiliaria(@RequestBody UnidadInmobiliariaDto unidaddto) {
        return ResponseEntity.ok(unidadservice.grabarUnidadInmobiliaria(unidaddto));
    }

    @GetMapping("/listar")
    public ResponseEntity<List<UnidadInmobiliariaDto>> getUnidadesInmobiliarias() {
        return ResponseEntity.ok(unidadservice.getUnidadesInmobiliarias());
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable("id") Long id) {
        unidadservice.eliminar(id);
        return ResponseEntity.ok("Unidad Inmobiliaria eliminada correctamente");
    }

    @PutMapping("/actualizar")
    public ResponseEntity<UnidadInmobiliariaDto> actualizar(@RequestBody UnidadInmobiliariaDto unidaddto) {
        UnidadInmobiliariaDto actualizado = unidadservice.actualizar(unidaddto);
        return ResponseEntity.ok(actualizado);
    }

    @GetMapping("/listarid/{id}")
    public ResponseEntity<UnidadInmobiliariaDto> obtenerPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(unidadservice.obtenerPorId(id));
    }
}
