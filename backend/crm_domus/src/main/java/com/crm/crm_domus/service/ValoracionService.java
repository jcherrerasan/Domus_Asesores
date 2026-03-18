package com.crm.crm_domus.service;

import com.crm.crm_domus.dto.request.ValoracionRequest;
import com.crm.crm_domus.dto.request.ValoracionUpdateRequest;
import com.crm.crm_domus.dto.response.ValoracionResponse;
import com.crm.crm_domus.exception.ResourceNotFoundException;
import com.crm.crm_domus.model.Propiedad;
import com.crm.crm_domus.model.Usuario;
import com.crm.crm_domus.model.Valoracion;
import com.crm.crm_domus.repository.PropiedadRepository;
import com.crm.crm_domus.repository.UsuarioRepository;
import com.crm.crm_domus.repository.ValoracionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ValoracionService {

    private final ValoracionRepository valoracionRepository;
    private final PropiedadRepository propiedadRepository;
    private final UsuarioRepository usuarioRepository;

    public ValoracionResponse create(ValoracionRequest request) {
        Valoracion valoracion = new Valoracion();
        applyRequest(valoracion, request);
        return mapToDTO(valoracionRepository.save(valoracion));
    }

    public List<ValoracionResponse> getByProperty(Long propiedadId) {
        return valoracionRepository.findByPropiedadId(propiedadId).stream().map(this::mapToDTO).toList();
    }

    public List<ValoracionResponse> getAll() {
        return valoracionRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    public ValoracionResponse getById(Long id) {
        Valoracion valoracion = valoracionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Valoracion no encontrada con id: " + id));
        return mapToDTO(valoracion);
    }

    public ValoracionResponse update(Long id, ValoracionUpdateRequest request) {
        Valoracion valoracion = valoracionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Valoracion no encontrada con id: " + id));
        valoracion.setTipo(request.getTipo());
        valoracion.setValorEstimadoAutomatico(request.getValorEstimadoAutomatico());
        valoracion.setValorRealEstimado(request.getValorRealEstimado());
        return mapToDTO(valoracionRepository.save(valoracion));
    }

    public void delete(Long id) {
        Valoracion valoracion = valoracionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Valoracion no encontrada con id: " + id));
        valoracionRepository.delete(valoracion);
    }

    private void applyRequest(Valoracion valoracion, ValoracionRequest request) {
        Propiedad propiedad = propiedadRepository.findById(request.getPropiedadId())
                .orElseThrow(() -> new ResourceNotFoundException("Propiedad no encontrada con id: " + request.getPropiedadId()));

        valoracion.setTipo(request.getTipo());
        valoracion.setValorEstimadoAutomatico(request.getValorEstimadoAutomatico());
        valoracion.setValorRealEstimado(request.getValorRealEstimado());
        valoracion.setPropiedad(propiedad);

        if (request.getAgenteId() != null) {
            Usuario agente = usuarioRepository.findById(request.getAgenteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Agente no encontrado con id: " + request.getAgenteId()));
            valoracion.setAgente(agente);
        } else {
            valoracion.setAgente(null);
        }
    }

    private ValoracionResponse mapToDTO(Valoracion valoracion) {
        return ValoracionResponse.builder()
                .id(valoracion.getId())
                .tipo(valoracion.getTipo())
                .valorEstimadoAutomatico(valoracion.getValorEstimadoAutomatico())
                .valorRealEstimado(valoracion.getValorRealEstimado())
                .urlValoracion(valoracion.getUrlValoracion())
                .creadoEn(valoracion.getCreatedAt())
                .propiedadId(valoracion.getPropiedad().getId())
                .agenteId(valoracion.getAgente() != null ? valoracion.getAgente().getId() : null)
                .nombreAgente(valoracion.getAgente() != null ? valoracion.getAgente().getNombre() : null)
                .build();
    }
}
