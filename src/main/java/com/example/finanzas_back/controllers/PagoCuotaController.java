package com.example.finanzas_back.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.finanzas_back.dtos.PagoCuotaDto;
import com.example.finanzas_back.interfaces.IPagoCuotaService;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:4200",
        allowCredentials = "true",
        exposedHeaders = "Authorization")

@RequestMapping("/pagoscuotas")
public class PagoCuotaController {
    @Autowired
    private IPagoCuotaService pagocuotaservice;

    @PostMapping("/registrar")
    public ResponseEntity<PagoCuotaDto> pagocuota(@RequestBody PagoCuotaDto pagocuotadto) {
        return ResponseEntity.ok(pagocuotaservice.grabarPagoCuota(pagocuotadto));
    }

    @GetMapping("/listar")
    public ResponseEntity<List<PagoCuotaDto>> getPagosCuotas() {
        return ResponseEntity.ok(pagocuotaservice.getPagosCuotas());
    }

    @DeleteMapping("/eliminar/{idPago}/{idCuota}")
    public ResponseEntity<String> eliminar(@PathVariable("idPago") Long idPago, @PathVariable("idCuota") Long idCuota) {
        pagocuotaservice.eliminar(idPago, idCuota);
        return ResponseEntity.ok("PagoCuota eliminado correctamente");
    }

    @PutMapping("/actualizar")
    public ResponseEntity<PagoCuotaDto> actualizar(@RequestBody PagoCuotaDto pagocuotadto) {
        PagoCuotaDto actualizado = pagocuotaservice.actualizar(pagocuotadto);
        return ResponseEntity.ok(actualizado);
    }

    @GetMapping("/listarid/{idPago}/{idCuota}")
    public ResponseEntity<PagoCuotaDto> obtenerPorId(@PathVariable("idPago") Long idPago, @PathVariable("idCuota") Long idCuota) {
        return ResponseEntity.ok(pagocuotaservice.obtenerPorId(idPago, idCuota));
    }
}
