package com.example.finanzas_back.repositories;

import com.example.finanzas_back.entities.Credito;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditoRepository extends JpaRepository<Credito, Long> {
}
