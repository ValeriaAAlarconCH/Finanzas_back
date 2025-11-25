package com.example.finanzas_back.dtos;

import com.example.finanzas_back.security.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDto implements Serializable {
    private Long id_cliente;
    private Integer dni;
    private String nombres;
    private String apellidos;
    private LocalDate fecha_nacimiento;
    private String sexo;
    private String direccion;
    private Integer telefono;
    private String email;
    private String ocupacion;
    private String empleador;
    private Double ingreso_mensual;
    private String estado_civil;
    private Integer num_dependientes;
    private String observaciones;

    private User user;
}
