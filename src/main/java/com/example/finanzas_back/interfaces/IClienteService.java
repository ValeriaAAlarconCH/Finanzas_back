package com.example.finanzas_back.interfaces;

import com.example.finanzas_back.dtos.ClienteDto;

import java.util.List;

public interface IClienteService {
    public ClienteDto grabarCliente(ClienteDto clientedto);
    public List<ClienteDto> getClientes();
    void eliminar(Long id);
    public ClienteDto actualizar(ClienteDto clientedto);
    public ClienteDto obtenerPorId(Long id);
}
