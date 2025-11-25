package com.example.finanzas_back.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.finanzas_back.dtos.PagoDto;
import com.example.finanzas_back.interfaces.IPagoService;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:4200",
        allowCredentials = "true",
        exposedHeaders = "Authorization")

@RequestMapping("/pagos")
public class PagoController {
    @Autowired
    private IPagoService pagoservice;

    @PostMapping("/registrar")
    public ResponseEntity<PagoDto> pago(@RequestBody PagoDto pagodto) {
        return ResponseEntity.ok(pagoservice.grabarPago(pagodto));
    }

    @GetMapping("/listar")
    public ResponseEntity<List<PagoDto>> getPagos() {
        return ResponseEntity.ok(pagoservice.getPagos());
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable("id") Long id) {
        pagoservice.eliminar(id);
        return ResponseEntity.ok("Pago eliminado correctamente");
    }

    @PutMapping("/actualizar")
    public ResponseEntity<PagoDto> actualizar(@RequestBody PagoDto pagodto) {
        PagoDto actualizado = pagoservice.actualizar(pagodto);
        return ResponseEntity.ok(actualizado);
    }

    @GetMapping("/listarid/{id}")
    public ResponseEntity<PagoDto> obtenerPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(pagoservice.obtenerPorId(id));
    }
}
