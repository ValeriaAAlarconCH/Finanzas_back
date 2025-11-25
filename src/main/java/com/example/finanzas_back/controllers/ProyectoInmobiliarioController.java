package com.example.finanzas_back.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.finanzas_back.dtos.ProyectoInmobiliarioDto;
import com.example.finanzas_back.interfaces.IProyectoInmobiliarioService;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:4200",
        allowCredentials = "true",
        exposedHeaders = "Authorization")

@RequestMapping("/proyectosinmobiliarios")
public class ProyectoInmobiliarioController {
    @Autowired
    private IProyectoInmobiliarioService proyectoservice;

    @PostMapping("/registrar")
    public ResponseEntity<ProyectoInmobiliarioDto> proyectoinmobiliario(@RequestBody ProyectoInmobiliarioDto proyectodto) {
        return ResponseEntity.ok(proyectoservice.grabarProyectoInmobiliario(proyectodto));
    }

    @GetMapping("/listar")
    public ResponseEntity<List<ProyectoInmobiliarioDto>> getProyectosInmobiliarios() {
        return ResponseEntity.ok(proyectoservice.getProyectosInmobiliarios());
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable("id") Long id) {
        proyectoservice.eliminar(id);
        return ResponseEntity.ok("Proyecto Inmobiliario eliminado correctamente");
    }

    @PutMapping("/actualizar")
    public ResponseEntity<ProyectoInmobiliarioDto> actualizar(@RequestBody ProyectoInmobiliarioDto proyectodto) {
        ProyectoInmobiliarioDto actualizado = proyectoservice.actualizar(proyectodto);
        return ResponseEntity.ok(actualizado);
    }

    @GetMapping("/listarid/{id}")
    public ResponseEntity<ProyectoInmobiliarioDto> obtenerPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(proyectoservice.obtenerPorId(id));
    }
}
