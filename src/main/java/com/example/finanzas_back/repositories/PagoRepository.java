package com.example.finanzas_back.repositories;

import com.example.finanzas_back.entities.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagoRepository extends JpaRepository<Pago, Long> {
}
