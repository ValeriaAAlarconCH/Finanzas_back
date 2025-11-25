package com.example.finanzas_back.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/registrar")
    public ResponseEntity<IndicadorFinancieroDto> indicadorfinanciero(@RequestBody IndicadorFinancieroDto indicadordto) {
        return ResponseEntity.ok(indicadorservice.grabarIndicadorFinanciero(indicadordto));
    }

    @GetMapping("/listar")
    public ResponseEntity<List<IndicadorFinancieroDto>> getIndicadoresFinancieros() {
        return ResponseEntity.ok(indicadorservice.getIndicadoresFinancieros());
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable("id") Long id) {
        indicadorservice.eliminar(id);
        return ResponseEntity.ok("Indicador Financiero eliminado correctamente");
    }

    @PutMapping("/actualizar")
    public ResponseEntity<IndicadorFinancieroDto> actualizar(@RequestBody IndicadorFinancieroDto indicadordto) {
        IndicadorFinancieroDto actualizado = indicadorservice.actualizar(indicadordto);
        return ResponseEntity.ok(actualizado);
    }

    @GetMapping("/listarid/{id}")
    public ResponseEntity<IndicadorFinancieroDto> obtenerPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(indicadorservice.obtenerPorId(id));
    }
}
