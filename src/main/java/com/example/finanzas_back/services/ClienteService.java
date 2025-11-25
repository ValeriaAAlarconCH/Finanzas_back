package com.example.finanzas_back.services;

import com.example.finanzas_back.security.entities.User;
import com.example.finanzas_back.security.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.finanzas_back.dtos.ClienteDto;
import com.example.finanzas_back.entities.Cliente;
import com.example.finanzas_back.interfaces.IClienteService;
import com.example.finanzas_back.repositories.ClienteRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClienteService implements IClienteService {
    @Autowired
    private ClienteRepository clienterepository;

    @Autowired
    private UserRepository userrepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ClienteDto grabarCliente(ClienteDto dto) {
        Cliente cliente = modelMapper.map(dto, Cliente.class);

        if (dto.getUser() != null && dto.getUser().getId() != null) {
            User user = userrepository.findById(dto.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + dto.getUser().getId()));
            cliente.setUser(user);
        } else {
            throw new RuntimeException("Debe proporcionar el ID del usuario");
        }

        Cliente guardado = clienterepository.save(cliente);
        return modelMapper.map(guardado, ClienteDto.class);
    }

    @Override
    public List<ClienteDto> getClientes() {
        List<Cliente> lista = clienterepository.findAll();
        List<ClienteDto> dtoLista = new ArrayList<>();

        for (Cliente c : lista) {
            ClienteDto dto = modelMapper.map(c, ClienteDto.class);
            dtoLista.add(dto);
        }

        return dtoLista;
    }

    @Override
    public void eliminar(Long id) {
        if (clienterepository.existsById(id)) {
            clienterepository.deleteById(id);
        } else {
            throw new RuntimeException("No se encontró el Cliente con ID: " + id);
        }
    }

    @Override
    public ClienteDto actualizar(ClienteDto clientedto) {
        Long id = clientedto.getId_cliente();
        if (id == null) {
            throw new RuntimeException("El ID del cliente no puede ser nulo");
        }

        Cliente clienteExistente = clienterepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el cliente con ID: " + id));

        clienteExistente.setDni(clientedto.getDni());
        clienteExistente.setNombres(clientedto.getNombres());
        clienteExistente.setApellidos(clientedto.getApellidos());
        clienteExistente.setFecha_nacimiento(clientedto.getFecha_nacimiento());
        clienteExistente.setSexo(clientedto.getSexo());
        clienteExistente.setDireccion(clientedto.getDireccion());
        clienteExistente.setTelefono(clientedto.getTelefono());
        clienteExistente.setEmail(clientedto.getEmail());
        clienteExistente.setOcupacion(clientedto.getOcupacion());
        clienteExistente.setEmpleador(clientedto.getEmpleador());
        clienteExistente.setIngreso_mensual(clientedto.getIngreso_mensual());
        clienteExistente.setEstado_civil(clientedto.getEstado_civil());
        clienteExistente.setNum_dependientes(clientedto.getNum_dependientes());
        clienteExistente.setObservaciones(clientedto.getObservaciones());

        if (clientedto.getUser() != null && clientedto.getUser().getId() != null) {
            User user = userrepository.findById(clientedto.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            clienteExistente.setUser(user);
        } else {
            throw new RuntimeException("Debe proporcionar el ID del usuario");
        }

        Cliente actualizado = clienterepository.save(clienteExistente);
        return modelMapper.map(actualizado, ClienteDto.class);
    }

    @Override
    public ClienteDto obtenerPorId(Long id) {
        Cliente cliente = clienterepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id));
        return modelMapper.map(cliente, ClienteDto.class);
    }
}
