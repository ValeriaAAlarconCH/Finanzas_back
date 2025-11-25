package com.example.finanzas_back.security.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.finanzas_back.security.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
