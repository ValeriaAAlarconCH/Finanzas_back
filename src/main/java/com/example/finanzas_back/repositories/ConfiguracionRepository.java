package com.example.finanzas_back.repositories;

import com.example.finanzas_back.entities.Configuracion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfiguracionRepository extends JpaRepository<Configuracion, Long> {
}
