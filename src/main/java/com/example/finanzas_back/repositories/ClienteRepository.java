package com.example.finanzas_back.repositories;

import com.example.finanzas_back.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
