package com.example.finanzas_back.repositories;

import com.example.finanzas_back.entities.Cuota;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuotaRepository extends JpaRepository<Cuota, Long> {
}
