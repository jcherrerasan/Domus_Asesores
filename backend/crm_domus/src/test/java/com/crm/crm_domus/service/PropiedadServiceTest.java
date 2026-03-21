package com.crm.crm_domus.service;

import com.crm.crm_domus.dto.request.PropiedadRequest;
import com.crm.crm_domus.dto.request.PropiedadUpdateRequest;
import com.crm.crm_domus.model.Cliente;
import com.crm.crm_domus.model.Extra;
import com.crm.crm_domus.model.Lead;
import com.crm.crm_domus.model.Propiedad;
import com.crm.crm_domus.model.Usuario;
import com.crm.crm_domus.model.enums.LeadTipo;
import com.crm.crm_domus.model.enums.OperacionTipo;
import com.crm.crm_domus.model.enums.PropiedadEstado;
import com.crm.crm_domus.model.enums.PropiedadTipo;
import com.crm.crm_domus.model.enums.Role;
import com.crm.crm_domus.repository.ClienteRepository;
import com.crm.crm_domus.repository.ExtraRepository;
import com.crm.crm_domus.repository.LeadRepository;
import com.crm.crm_domus.repository.PropiedadRepository;
import com.crm.crm_domus.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PropiedadServiceTest {

    @Mock
    private PropiedadRepository propiedadRepository;

    @Mock
    private ExtraRepository extraRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private LeadRepository leadRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private PropiedadService propiedadService;

    @Test
    void createCreatesMissingExtrasAndMapsOwnerData() {
        PropiedadRequest request = buildRequest();
        Usuario agente = Usuario.builder().id(8L).nombre("Agente").role(Role.AGENTE).build();
        Lead lead = Lead.builder().id(4L).nombre("Lead owner").tipo(LeadTipo.PROPIETARIO).build();
        Cliente cliente = Cliente.builder().id(6L).lead(lead).nombre("Cliente owner").build();

        when(usuarioRepository.findById(8L)).thenReturn(Optional.of(agente));
        when(leadRepository.findById(4L)).thenReturn(Optional.of(lead));
        when(clienteRepository.findById(6L)).thenReturn(Optional.of(cliente));
        when(extraRepository.findByNombre("TERRAZA")).thenReturn(Optional.empty());
        when(extraRepository.findByNombre("ASCENSOR")).thenReturn(Optional.of(Extra.builder().id(3L).nombre("ASCENSOR").build()));
        when(extraRepository.save(any(Extra.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(propiedadRepository.save(any(Propiedad.class))).thenAnswer(invocation -> {
            Propiedad saved = invocation.getArgument(0);
            saved.setId(11L);
            return saved;
        });

        var response = propiedadService.create(request);

        assertThat(response.getLeadPropietario().getId()).isEqualTo(4L);
        assertThat(response.getPropietario().getId()).isEqualTo(6L);
        assertThat(response.getExtras()).containsExactlyInAnyOrder("TERRAZA", "ASCENSOR");
    }

    @Test
    void createRejectsOwnerClientThatDoesNotMatchLead() {
        PropiedadRequest request = buildRequest();
        Usuario agente = Usuario.builder().id(8L).role(Role.AGENTE).build();
        Lead requestLead = Lead.builder().id(4L).build();
        Lead differentLead = Lead.builder().id(99L).build();
        Cliente cliente = Cliente.builder().id(6L).lead(differentLead).build();

        when(usuarioRepository.findById(8L)).thenReturn(Optional.of(agente));
        when(leadRepository.findById(4L)).thenReturn(Optional.of(requestLead));
        when(clienteRepository.findById(6L)).thenReturn(Optional.of(cliente));

        assertThatThrownBy(() -> propiedadService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("lead propietario");
    }

    @Test
    void getExtrasByPropiedadIdReturnsOrderedNames() {
        Propiedad propiedad = Propiedad.builder()
                .id(3L)
                .extras(new LinkedHashSet<>(Set.of(
                        Extra.builder().nombre("TERRAZA").build(),
                        Extra.builder().nombre("GARAJE").build()
                )))
                .build();

        when(propiedadRepository.findById(3L)).thenReturn(Optional.of(propiedad));

        var extras = propiedadService.getExtrasByPropiedadId(3L);

        assertThat(extras).contains("TERRAZA", "GARAJE");
    }

    @Test
    void searchPropiedadesDelegatesToRepositoryWithPagination() {
        Propiedad propiedad = Propiedad.builder()
                .id(20L)
                .precio(250000.0)
                .ciudad("Madrid")
                .propiedadTipo(PropiedadTipo.PISO)
                .operacionTipo(OperacionTipo.VENTA)
                .estado(PropiedadEstado.PUBLICADA)
                .build();

        when(propiedadRepository.searchPropiedades(
                eq("Madrid"),
                eq(200000.0),
                eq(300000.0),
                eq(3),
                eq(2),
                eq(100.0),
                eq(PropiedadTipo.PISO),
                eq(OperacionTipo.VENTA),
                eq(List.of("terraza")),
                any(Pageable.class)
        )).thenReturn(new PageImpl<>(List.of(propiedad)));

        var page = propiedadService.searchPropiedades(
                "Madrid", 200000.0, 300000.0, 3, 2, 100.0,
                PropiedadTipo.PISO, OperacionTipo.VENTA, List.of("terraza"),
                0, 10, "precio", "asc"
        );

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().getFirst().getCiudad()).isEqualTo("Madrid");
    }

    @Test
    void updatePropiedadReplacesExtras() {
        Propiedad propiedad = Propiedad.builder().id(5L).build();
        PropiedadUpdateRequest request = new PropiedadUpdateRequest();
        request.setPrecio(300000.0);
        request.setCiudad("Madrid");
        request.setDireccion("Nueva 2");
        request.setCodigoPostal("28001");
        request.setHabitaciones(4);
        request.setBanios(2);
        request.setMetrosConstruidos(120.0);
        request.setMetrosUtiles(100.0);
        request.setPropiedadTipo(PropiedadTipo.PISO);
        request.setOperacionTipo(OperacionTipo.VENTA);
        request.setEstado(PropiedadEstado.RESERVADA);
        request.setExtras(List.of("TERRAZA"));

        when(propiedadRepository.findById(5L)).thenReturn(Optional.of(propiedad));
        when(extraRepository.findByNombre("TERRAZA")).thenReturn(Optional.of(Extra.builder().nombre("TERRAZA").build()));
        when(propiedadRepository.save(any(Propiedad.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = propiedadService.updatePropiedad(5L, request);

        assertThat(response.getEstado()).isEqualTo(PropiedadEstado.RESERVADA);
        assertThat(response.getExtras()).containsExactly("TERRAZA");
    }

    private PropiedadRequest buildRequest() {
        PropiedadRequest request = new PropiedadRequest();
        request.setPrecio(250000.0);
        request.setCiudad("Madrid");
        request.setDireccion("Mayor 1");
        request.setCodigoPostal("28001");
        request.setHabitaciones(3);
        request.setBanios(2);
        request.setMetrosConstruidos(120.0);
        request.setMetrosUtiles(100.0);
        request.setPropiedadTipo(PropiedadTipo.PISO);
        request.setOperacionTipo(OperacionTipo.VENTA);
        request.setEstado(PropiedadEstado.PUBLICADA);
        request.setPropietarioClienteId(6L);
        request.setPropietarioLeadId(4L);
        request.setAgenteAsignadoId(8L);
        request.setExtras(List.of("TERRAZA", "ASCENSOR"));
        return request;
    }
}
