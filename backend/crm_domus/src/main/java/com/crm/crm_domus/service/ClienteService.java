package com.crm.crm_domus.service;

import com.crm.crm_domus.dto.request.ClienteRequest;
import com.crm.crm_domus.dto.request.ClienteUpdateRequest;
import com.crm.crm_domus.dto.response.ClienteResponse;
import com.crm.crm_domus.exception.ResourceNotFoundException;
import com.crm.crm_domus.model.Cliente;
import com.crm.crm_domus.model.Lead;
import com.crm.crm_domus.repository.ClienteRepository;
import com.crm.crm_domus.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final LeadRepository leadRepository;

    public ClienteResponse create(ClienteRequest request) {
        Lead lead = leadRepository.findById(request.getLeadId())
                .orElseThrow(() -> new ResourceNotFoundException("Lead no encontrado con id: " + request.getLeadId()));

        if (clienteRepository.existsByLeadId(lead.getId())) {
            throw new IllegalArgumentException("Este lead ya esta convertido en cliente");
        }

        if (clienteRepository.findByDni(request.getDni()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un cliente con ese DNI");
        }

        Cliente cliente = new Cliente();
        cliente.setDni(request.getDni());
        cliente.setDireccion(request.getDireccion());
        syncClienteWithLead(cliente, lead);

        return mapToDTO(clienteRepository.save(cliente));
    }

    public List<ClienteResponse> getAllClientes() {
        return clienteRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public ClienteResponse getClienteById(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));
        return mapToDTO(cliente);
    }

    public ClienteResponse updateCliente(Long id, ClienteUpdateRequest request) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));

        clienteRepository.findByDni(request.getDni())
                .filter(existente -> !existente.getId().equals(id))
                .ifPresent(existente -> {
                    throw new IllegalArgumentException("Ya existe un cliente con ese DNI");
                });

        cliente.setNombre(request.getNombre());
        cliente.setDni(request.getDni());
        cliente.setTelefono(request.getTelefono());
        cliente.setEmail(request.getEmail());
        cliente.setTipo(request.getTipo());
        cliente.setDireccion(request.getDireccion());
        cliente.setDescripcion(request.getDescripcion());

        return mapToDTO(clienteRepository.save(cliente));
    }

    public void deleteCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));
        clienteRepository.delete(cliente);
    }

    public List<ClienteResponse> searchClientes(String nombre, String telefono, String email) {
        return clienteRepository.searchClientes(nombre, telefono, email)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    private void syncClienteWithLead(Cliente cliente, Lead lead) {
        cliente.setLead(lead);
        cliente.setNombre(lead.getNombre());
        cliente.setTelefono(lead.getTelefono());
        cliente.setEmail(lead.getEmail());
        cliente.setTipo(lead.getTipo());
        cliente.setDescripcion(lead.getDescripcion());
        cliente.setAsignadoAgente(lead.getAsignadoA());
    }

    private ClienteResponse mapToDTO(Cliente cliente) {
        return ClienteResponse.builder()
                .id(cliente.getId())
                .leadId(cliente.getLead() != null ? cliente.getLead().getId() : null)
                .nombre(cliente.getNombre())
                .dni(cliente.getDni())
                .telefono(cliente.getTelefono())
                .email(cliente.getEmail())
                .tipo(cliente.getTipo())
                .direccion(cliente.getDireccion())
                .descripcion(cliente.getDescripcion())
                .build();
    }
}
