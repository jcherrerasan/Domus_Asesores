package com.crm.crm_domus.service;

import com.crm.crm_domus.dto.request.ClienteRequest;
import com.crm.crm_domus.dto.request.ClienteUpdateRequest;
import com.crm.crm_domus.exception.ResourceNotFoundException;
import com.crm.crm_domus.model.Cliente;
import com.crm.crm_domus.model.Lead;
import com.crm.crm_domus.model.Usuario;
import com.crm.crm_domus.model.enums.LeadEstado;
import com.crm.crm_domus.model.enums.LeadTipo;
import com.crm.crm_domus.model.enums.Role;
import com.crm.crm_domus.repository.ClienteRepository;
import com.crm.crm_domus.repository.LeadRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private LeadRepository leadRepository;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    void createCopiesLeadDataIntoClient() {
        ClienteRequest request = new ClienteRequest();
        request.setLeadId(2L);
        request.setDni("12345678A");
        request.setDireccion("Gran Via 1");

        Usuario agente = Usuario.builder().id(5L).nombre("Agente").role(Role.AGENTE).build();
        Lead lead = Lead.builder()
                .id(2L)
                .nombre("Carlos Lopez")
                .telefono("600123456")
                .email("carlos@test.com")
                .tipo(LeadTipo.PROPIETARIO)
                .estado(LeadEstado.NUEVO)
                .descripcion("Lead original")
                .asignadoA(agente)
                .build();

        when(leadRepository.findById(2L)).thenReturn(Optional.of(lead));
        when(clienteRepository.existsByLeadId(2L)).thenReturn(false);
        when(clienteRepository.findByDni("12345678A")).thenReturn(Optional.empty());
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> {
            Cliente saved = invocation.getArgument(0);
            saved.setId(9L);
            return saved;
        });

        var response = clienteService.create(request);

        assertThat(response.getLeadId()).isEqualTo(2L);
        assertThat(response.getNombre()).isEqualTo("Carlos Lopez");
        assertThat(response.getTipo()).isEqualTo(LeadTipo.PROPIETARIO);
        assertThat(response.getDireccion()).isEqualTo("Gran Via 1");
    }

    @Test
    void createFailsWhenLeadAlreadyConverted() {
        ClienteRequest request = new ClienteRequest();
        request.setLeadId(2L);
        request.setDni("12345678A");
        request.setDireccion("Gran Via 1");

        when(leadRepository.findById(2L)).thenReturn(Optional.of(Lead.builder().id(2L).build()));
        when(clienteRepository.existsByLeadId(2L)).thenReturn(true);

        assertThatThrownBy(() -> clienteService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("convertido");
    }

    @Test
    void updateClienteRejectsDuplicatedDniFromAnotherClient() {
        Cliente existing = Cliente.builder().id(1L).dni("11111111A").build();
        Cliente duplicated = Cliente.builder().id(2L).dni("22222222B").build();

        ClienteUpdateRequest request = new ClienteUpdateRequest();
        request.setNombre("Laura");
        request.setDni("22222222B");
        request.setTelefono("600000000");
        request.setEmail("laura@test.com");
        request.setTipo(LeadTipo.COMPRADOR);
        request.setDireccion("Nueva 2");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(clienteRepository.findByDni("22222222B")).thenReturn(Optional.of(duplicated));

        assertThatThrownBy(() -> clienteService.updateCliente(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("DNI");
    }

    @Test
    void getClienteByIdThrowsWhenMissing() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clienteService.getClienteById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}
