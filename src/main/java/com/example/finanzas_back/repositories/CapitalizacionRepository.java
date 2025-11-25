package com.example.finanzas_back.repositories;

import com.example.finanzas_back.entities.Capitalizacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CapitalizacionRepository extends JpaRepository<Capitalizacion, Long> {
}
