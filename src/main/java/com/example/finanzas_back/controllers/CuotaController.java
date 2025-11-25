package com.example.finanzas_back.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.finanzas_back.dtos.CuotaDto;
import com.example.finanzas_back.interfaces.ICuotaService;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:4200",
        allowCredentials = "true",
        exposedHeaders = "Authorization")

@RequestMapping("/cuotas")
public class CuotaController {
    @Autowired
    private ICuotaService cuotaservice;

    @PostMapping("/registrar")
    public ResponseEntity<CuotaDto> cuota(@RequestBody CuotaDto cuotadto) {
        return ResponseEntity.ok(cuotaservice.grabarCuota(cuotadto));
    }

    @GetMapping("/listar")
    public ResponseEntity<List<CuotaDto>> getCuotas() {
        return ResponseEntity.ok(cuotaservice.getCuotas());
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable("id") Long id) {
        cuotaservice.eliminar(id);
        return ResponseEntity.ok("Cuota eliminada correctamente");
    }

    @PutMapping("/actualizar")
    public ResponseEntity<CuotaDto> actualizar(@RequestBody CuotaDto cuotadto) {
        CuotaDto actualizado = cuotaservice.actualizar(cuotadto);
        return ResponseEntity.ok(actualizado);
    }

    @GetMapping("/listarid/{id}")
    public ResponseEntity<CuotaDto> obtenerPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(cuotaservice.obtenerPorId(id));
    }
}
