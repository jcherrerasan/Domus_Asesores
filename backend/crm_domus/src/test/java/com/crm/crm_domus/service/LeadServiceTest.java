package com.crm.crm_domus.service;

import com.crm.crm_domus.dto.request.LeadCreateRequest;
import com.crm.crm_domus.dto.request.LeadRequest;
import com.crm.crm_domus.exception.ResourceNotFoundException;
import com.crm.crm_domus.model.Lead;
import com.crm.crm_domus.model.Usuario;
import com.crm.crm_domus.model.enums.LeadEstado;
import com.crm.crm_domus.model.enums.LeadTipo;
import com.crm.crm_domus.model.enums.Role;
import com.crm.crm_domus.repository.LeadRepository;
import com.crm.crm_domus.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeadServiceTest {

    @Mock
    private LeadRepository leadRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private LeadService leadService;

    @Test
    void createUsesDefaultAdminWhenIdsAreMissing() {
        LeadCreateRequest request = new LeadCreateRequest();
        request.setNombre("Carlos");
        request.setTelefono("600123456");
        request.setEmail("carlos@test.com");
        request.setTipo(LeadTipo.PROPIETARIO);
        request.setDescripcion("Interesado");

        Usuario admin = Usuario.builder()
                .id(99L)
                .nombre("Administrador")
                .email("administrador@domus.local")
                .password("admin1234")
                .role(Role.ADMINISTRADOR)
                .activo(true)
                .build();

        when(usuarioRepository.findByEmail("administrador@domus.local")).thenReturn(Optional.of(admin));
        when(leadRepository.save(any(Lead.class))).thenAnswer(invocation -> {
            Lead saved = invocation.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        var response = leadService.create(request);

        assertThat(response.getEstado()).isEqualTo(LeadEstado.NUEVO);
        assertThat(response.getCreadoPor().getEmail()).isEqualTo("administrador@domus.local");
        assertThat(response.getAsignadoA().getId()).isEqualTo(99L);
    }

    @Test
    void createFailsWhenAssigneeDoesNotExist() {
        LeadCreateRequest request = new LeadCreateRequest();
        request.setNombre("Carlos");
        request.setTelefono("600123456");
        request.setTipo(LeadTipo.PROPIETARIO);
        request.setAsignadoAId(25L);

        when(usuarioRepository.findById(25L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> leadService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("25");
    }

    @Test
    void searchLeadsMapsRepositoryResults() {
        Lead lead = Lead.builder()
                .id(3L)
                .nombre("Carlos")
                .telefono("600123456")
                .email("carlos@test.com")
                .tipo(LeadTipo.PROPIETARIO)
                .estado(LeadEstado.INTERESADO)
                .build();

        when(leadRepository.searchLeads("Carlos", null, null)).thenReturn(List.of(lead));

        var response = leadService.searchLeads("Carlos", null, null);

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().getEstado()).isEqualTo(LeadEstado.INTERESADO);
        assertThat(response.getFirst().getNombre()).isEqualTo("Carlos");
    }

    @Test
    void updateLeadPreservesEstadoWhenRequestDoesNotIncludeOne() {
        Lead existing = Lead.builder()
                .id(5L)
                .nombre("Anterior")
                .telefono("600000000")
                .email("old@test.com")
                .tipo(LeadTipo.COMPRADOR)
                .estado(LeadEstado.CONTACTADO)
                .build();
        LeadRequest request = new LeadRequest();
        request.setNombre("Nuevo");
        request.setTelefono("611111111");
        request.setEmail("new@test.com");
        request.setTipo(LeadTipo.INQUILINO);

        when(leadRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(leadRepository.save(any(Lead.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = leadService.updateLead(5L, request);

        assertThat(response.getEstado()).isEqualTo(LeadEstado.CONTACTADO);
        assertThat(response.getTipo()).isEqualTo(LeadTipo.INQUILINO);
    }
}
