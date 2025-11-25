package com.example.finanzas_back.repositories;

import com.example.finanzas_back.entities.Moneda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonedaRepository extends JpaRepository<Moneda, Long> {
}
