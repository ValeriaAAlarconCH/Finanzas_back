package com.example.finanzas_back.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // ⬅Importación clave
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

    /**
     * Permite registrar un nuevo cliente.
     * Acceso: Solo para ADMIN.
     */
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/registrar")
    public ResponseEntity<ClienteDto> cliente(@RequestBody ClienteDto clientedto) {
        return ResponseEntity.ok(clienteservice.grabarCliente(clientedto));
    }

    /**
     * Permite listar todos los clientes.
     * Acceso: ADMIN y ANALISTA (ambos necesitan ver el portafolio completo).
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ANALISTA')")
    @GetMapping("/listar")
    public ResponseEntity<List<ClienteDto>> getClientes() {
        return ResponseEntity.ok(clienteservice.getClientes());
    }

    /**
     * Permite eliminar un cliente.
     * Acceso: Solo para ADMIN (operación destructiva).
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable("id") Long id) {
        clienteservice.eliminar(id);
        return ResponseEntity.ok("Cliente eliminado correctamente");
    }

    /**
     * Permite actualizar un cliente existente.
     * Acceso: Solo para ADMIN.
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/actualizar")
    public ResponseEntity<ClienteDto> actualizar(@RequestBody ClienteDto clientedto) {
        ClienteDto actualizado = clienteservice.actualizar(clientedto);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * Permite obtener un cliente por su ID.
     * Acceso: ADMIN, ANALISTA, y USER (para que un usuario pueda ver sus propios datos o ser referenciado).
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'ANALISTA', 'USER')")
    @GetMapping("/listarid/{id}")
    public ResponseEntity<ClienteDto> obtenerPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(clienteservice.obtenerPorId(id));
    }
}