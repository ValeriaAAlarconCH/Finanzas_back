package com.example.finanzas_back.repositories;

import com.example.finanzas_back.entities.EntidadFinanciera;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntidadFinancieraRepository extends JpaRepository<EntidadFinanciera, Long> {
}
