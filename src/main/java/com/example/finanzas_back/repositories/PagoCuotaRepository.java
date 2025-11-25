package com.example.finanzas_back.repositories;

import com.example.finanzas_back.entities.PagoCuota;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagoCuotaRepository extends JpaRepository<PagoCuota, PagoCuota.PagoCuotaId> {
}
