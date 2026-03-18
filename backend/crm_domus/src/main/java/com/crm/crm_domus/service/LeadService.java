package com.crm.crm_domus.service;

import com.crm.crm_domus.dto.request.LeadCreateRequest;
import com.crm.crm_domus.dto.request.LeadRequest;
import com.crm.crm_domus.dto.response.LeadResponse;
import com.crm.crm_domus.dto.response.UsuarioResponse;
import com.crm.crm_domus.exception.ResourceNotFoundException;
import com.crm.crm_domus.model.Lead;
import com.crm.crm_domus.model.Usuario;
import com.crm.crm_domus.model.enums.LeadEstado;
import com.crm.crm_domus.model.enums.Role;
import com.crm.crm_domus.repository.LeadRepository;
import com.crm.crm_domus.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeadService {

    private final LeadRepository leadRepository;
    private final UsuarioRepository usuarioRepository;

    public LeadResponse create(LeadCreateRequest request) {
        Lead lead = new Lead();
        Usuario creadoPor = resolveCreadoPor(request.getCreadoPorId());
        Usuario asignadoA = resolveAsignadoA(request.getAsignadoAId(), creadoPor);

        lead.setNombre(request.getNombre());
        lead.setTelefono(request.getTelefono());
        lead.setEmail(request.getEmail());
        lead.setTipo(request.getTipo());
        lead.setDescripcion(request.getDescripcion());
        lead.setCreadoPor(creadoPor);
        lead.setAsignadoA(asignadoA);
        lead.setEstado(LeadEstado.NUEVO);

        return mapToDTO(leadRepository.save(lead));
    }

    public List<LeadResponse> getAllLeads() {
        return leadRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public LeadResponse getLeadById(Long id) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead no encontrado con id: " + id));
        return mapToDTO(lead);
    }

    public LeadResponse updateLead(Long id, LeadRequest request) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead no encontrado con id: " + id));

        lead.setNombre(request.getNombre());
        lead.setTelefono(request.getTelefono());
        lead.setEmail(request.getEmail());
        lead.setTipo(request.getTipo());
        lead.setDescripcion(request.getDescripcion());

        if (request.getEstado() != null) {
            lead.setEstado(request.getEstado());
        }

        return mapToDTO(leadRepository.save(lead));
    }

    public void deleteLead(Long id) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead no encontrado con id: " + id));
        leadRepository.delete(lead);
    }

    public List<LeadResponse> searchLeads(String nombre, String telefono, String email) {
        return leadRepository.searchLeads(nombre, telefono, email)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    private LeadResponse mapToDTO(Lead lead) {
        return LeadResponse.builder()
                .id(lead.getId())
                .nombre(lead.getNombre())
                .telefono(lead.getTelefono())
                .email(lead.getEmail())
                .tipo(lead.getTipo())
                .estado(lead.getEstado())
                .descripcion(lead.getDescripcion())
                .creadoPor(mapUsuario(lead.getCreadoPor()))
                .asignadoA(mapUsuario(lead.getAsignadoA()))
                .build();
    }

    private UsuarioResponse mapUsuario(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .role(usuario.getRole())
                .activo(usuario.getActivo())
                .build();
    }

    private Usuario resolveCreadoPor(Long creadoPorId) {
        if (creadoPorId != null) {
            return usuarioRepository.findById(creadoPorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + creadoPorId));
        }

        return getOrCreateDefaultAdmin();
    }

    private Usuario resolveAsignadoA(Long asignadoAId, Usuario creadoPor) {
        if (asignadoAId != null) {
            return usuarioRepository.findById(asignadoAId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + asignadoAId));
        }

        return creadoPor != null ? creadoPor : getOrCreateDefaultAdmin();
    }

    private Usuario getOrCreateDefaultAdmin() {
        return usuarioRepository.findByEmail("administrador@domus.local")
                .or(() -> usuarioRepository.findByRole(Role.ADMINISTRADOR).stream().findFirst())
                .orElseGet(() -> usuarioRepository.save(
                        Usuario.builder()
                                .nombre("Administrador")
                                .email("administrador@domus.local")
                                .password("admin1234")
                                .role(Role.ADMINISTRADOR)
                                .activo(true)
                                .build()
                ));
    }
}
