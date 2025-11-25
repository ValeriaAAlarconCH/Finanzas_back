package com.example.finanzas_back.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.finanzas_back.dtos.ClienteDto;
import com.example.finanzas_back.interfaces.IClienteService;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:4200",
        allowCredentials = "true",
        exposedHeaders = "Authorization")

@RequestMapping("/clientes")
public class ClienteController {
    @Autowired
    private IClienteService clienteservice;

    @PostMapping("/registrar")
    public ResponseEntity<ClienteDto> cliente(@RequestBody ClienteDto clientedto) {
        return ResponseEntity.ok(clienteservice.grabarCliente(clientedto));
    }

    @GetMapping("/listar")
    public ResponseEntity<List<ClienteDto>> getClientes() {
        return ResponseEntity.ok(clienteservice.getClientes());
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable("id") Long id) {
        clienteservice.eliminar(id);
        return ResponseEntity.ok("Cliente eliminado correctamente");
    }

    @PutMapping("/actualizar")
    public ResponseEntity<ClienteDto> actualizar(@RequestBody ClienteDto clientedto) {
        ClienteDto actualizado = clienteservice.actualizar(clientedto);
        return ResponseEntity.ok(actualizado);
    }

    @GetMapping("/listarid/{id}")
    public ResponseEntity<ClienteDto> obtenerPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(clienteservice.obtenerPorId(id));
    }
}
