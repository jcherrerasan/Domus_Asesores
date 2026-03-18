package com.crm.crm_domus;

import com.crm.crm_domus.model.Cliente;
import com.crm.crm_domus.model.Extra;
import com.crm.crm_domus.model.Lead;
import com.crm.crm_domus.model.Usuario;
import com.crm.crm_domus.model.enums.LeadEstado;
import com.crm.crm_domus.model.enums.LeadTipo;
import com.crm.crm_domus.model.enums.Role;
import com.crm.crm_domus.repository.ClienteRepository;
import com.crm.crm_domus.repository.ExtraRepository;
import com.crm.crm_domus.repository.LeadRepository;
import com.crm.crm_domus.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ApiIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ExtraRepository extraRepository;

    @Test
    void userCrudFlowWorks() throws Exception {
        String createPayload = """
                {
                  "nombre": "Marc Agent",
                  "email": "marc.agent@test.com",
                  "password": "password123",
                  "role": "AGENTE"
                }
                """;

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("marc.agent@test.com"));

        long userId = usuarioRepository.findByEmail("marc.agent@test.com")
                .orElseThrow()
                .getId();

        mockMvc.perform(get("/api/usuarios/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Marc Agent"));

        String updatePayload = """
                {
                  "nombre": "Marc Coordinador",
                  "email": "marc.agent@test.com",
                  "password": "password123",
                  "role": "COORDINADOR"
                }
                """;

        mockMvc.perform(put("/api/usuarios/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Marc Coordinador"))
                .andExpect(jsonPath("$.role").value("COORDINADOR"));

        mockMvc.perform(delete("/api/usuarios/{id}", userId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/usuarios/{id}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void rootRedirectsToSwaggerUi() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/swagger-ui/index.html"));
    }

    @Test
    void unknownRouteReturnsNotFoundInsteadOfServerError() throws Exception {
        mockMvc.perform(get("/ruta-inexistente"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("No encontrado"));
    }

    @Test
    void invalidUserReturnsBadRequest() throws Exception {
        String invalidPayload = """
                {
                  "nombre": "",
                  "email": "email-invalido",
                  "password": "",
                  "role": "AGENTE"
                }
                """;

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Error de validacion"))
                .andExpect(jsonPath("$.messages.nombre").exists());
    }

    @Test
    void userWithoutRoleReturnsBadRequest() throws Exception {
        String invalidPayload = """
                {
                  "nombre": "Marc Agent",
                  "email": "marc.sinrol@test.com",
                  "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Error de validacion"))
                .andExpect(jsonPath("$.messages.role").exists());
    }

    @Test
    void leadWithoutRequiredFieldsReturnsBadRequest() throws Exception {
        String invalidPayload = """
                {
                  "nombre": "Pedro Acosta",
                  "email": "paco37@email.com"
                }
                """;

        mockMvc.perform(post("/api/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Error de validacion"))
                .andExpect(jsonPath("$.messages.telefono").exists())
                .andExpect(jsonPath("$.messages.tipo").exists());
    }

    @Test
    void leadCreationDefaultsCreatedByAndAssignedToToAdmin() throws Exception {
        String payload = """
                {
                  "nombre": "Carlos Lopez",
                  "telefono": "600123456",
                  "email": "carlos@email.com",
                  "tipo": "PROPIETARIO",
                  "descripcion": "Interesado en vender"
                }
                """;

        mockMvc.perform(post("/api/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.creadoPor.email").value("administrador@domus.local"))
                .andExpect(jsonPath("$.asignadoA.email").value("administrador@domus.local"));
    }

    @Test
    void leadCreationUsesProvidedUsersForCreatorAndAssignee() throws Exception {
        Usuario creator = usuarioRepository.save(Usuario.builder()
                 .nombre("Laura Creadora")
                .email("laura.creator@test.com")
                .password("password123")
                .role(Role.ADMINISTRADOR)
                .build());

        Usuario assignee = usuarioRepository.save(Usuario.builder()
                 .nombre("Ana Asesora")
                .email("ana.assignee@test.com")
                .password("password123")
                .role(Role.AGENTE)
                .build());

        String payload = """
                {
                  "nombre": "Carlos Lopez",
                  "telefono": "600123456",
                  "email": "carlos@email.com",
                  "tipo": "PROPIETARIO",
                  "descripcion": "Interesado en vender",
                  "creadoPorId": %d,
                  "asignadoAId": %d
                }
                """.formatted(creator.getId(), assignee.getId());

        mockMvc.perform(post("/api/leads")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.creadoPor.email").value("laura.creator@test.com"))
                .andExpect(jsonPath("$.asignadoA.email").value("ana.assignee@test.com"));
    }

    @Test
    void accountWithoutRequiredFieldsReturnsBadRequest() throws Exception {
        Lead lead = leadRepository.save(Lead.builder()
                 .nombre("Fabio Quartararo")
                 .telefono("600123456")
                .email("eldiablo20.owner@test.com")
                 .tipo(LeadTipo.PROPIETARIO)
                 .estado(LeadEstado.NUEVO)
                 .descripcion("Propietario de inmueble")
                .build());

        String invalidPayload = """
                {
                  "leadId": %d
                }
                """.formatted(lead.getId());

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Error de validacion"))
                .andExpect(jsonPath("$.messages.dni").exists())
                .andExpect(jsonPath("$.messages.direccion").exists());
    }

    @Test
    void accountCreationUsesLeadDataAndOnlyRequiresDniAndAddress() throws Exception {
        Usuario agent = usuarioRepository.save(Usuario.builder()
                 .nombre("Alex Marquez")
                .email("alex.accounts@test.com")
                .password("password123")
                .role(Role.AGENTE)
                .build());

        Lead lead = leadRepository.save(Lead.builder()
                 .nombre("Fabio Quartararo")
                 .telefono("600123456")
                .email("eldiablo20.owner@test.com")
                 .tipo(LeadTipo.ARRENDADOR)
                 .estado(LeadEstado.NUEVO)
                 .descripcion("Propietario interesado en alquilar")
                .asignadoA(agent)
                .build());

        String payload = """
                {
                  "leadId": %d,
                  "dni": "12345678Z",
                  "direccion": "Calle Mayor 10, Madrid"
                }
                """.formatted(lead.getId());

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.leadId").value(lead.getId()))
                .andExpect(jsonPath("$.nombre").value("Fabio Quartararo"))
                .andExpect(jsonPath("$.telefono").value("600123456"))
                .andExpect(jsonPath("$.email").value("eldiablo20.owner@test.com"))
                .andExpect(jsonPath("$.tipo").value("ARRENDADOR"))
                .andExpect(jsonPath("$.direccion").value("Calle Mayor 10, Madrid"))
                .andExpect(jsonPath("$.descripcion").value("Propietario interesado en alquilar"));
    }

    @Test
    void accountUpdateAllowsEditingAllClientFields() throws Exception {
        Lead lead = leadRepository.save(Lead.builder()
                 .nombre("Carlos Lopez")
                 .telefono("600123456")
                .email("carlos@test.com")
                 .tipo(LeadTipo.PROPIETARIO)
                 .estado(LeadEstado.NUEVO)
                 .descripcion("Descripcion inicial")
                .build());

        String createPayload = """
                {
                  "leadId": %d,
                  "dni": "12345678Z",
                  "direccion": "Calle Mayor 10, Madrid"
                }
                """.formatted(lead.getId());

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createPayload))
                .andExpect(status().isOk());

        long accountId = clienteRepository.findByDni("12345678Z")
                .orElseThrow()
                .getId();

        String updatePayload = """
                {
                  "nombre": "Carlos Lopez Editado",
                  "dni": "87654321X",
                  "telefono": "699000111",
                  "email": "editado@test.com",
                  "tipo": "COMPRADOR",
                  "direccion": "Gran Via 20, Madrid",
                  "descripcion": "Cliente actualizado manualmente"
                }
                """;

        mockMvc.perform(put("/api/clientes/{id}", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Carlos Lopez Editado"))
                .andExpect(jsonPath("$.dni").value("87654321X"))
                .andExpect(jsonPath("$.telefono").value("699000111"))
                .andExpect(jsonPath("$.email").value("editado@test.com"))
                .andExpect(jsonPath("$.tipo").value("COMPRADOR"))
                .andExpect(jsonPath("$.direccion").value("Gran Via 20, Madrid"))
                .andExpect(jsonPath("$.descripcion").value("Cliente actualizado manualmente"));
    }

    @Test
    void valuationWithoutRequiredFieldsReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/valoraciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Error de validacion"))
                .andExpect(jsonPath("$.messages.tipo").exists())
                .andExpect(jsonPath("$.messages.propiedadId").exists());
    }

    @Test
    void propertySearchWithPaginationWorks() throws Exception {
        Usuario agent = usuarioRepository.save(Usuario.builder()
                 .nombre("Alex Gestora")
                .email("alex.gestor@test.com")
                .password("password123")
                .role(Role.AGENTE)
                .build());

        Lead lead = leadRepository.save(Lead.builder()
                 .nombre("Fabio Quartararo")
                 .telefono("600123456")
                .email("eldiablo20.owner@test.com")
                 .tipo(LeadTipo.PROPIETARIO)
                 .estado(LeadEstado.NUEVO)
                 .descripcion("Propietario de inmueble")
                .build());

        Cliente owner = clienteRepository.save(Cliente.builder()
                .lead(lead)
                 .nombre("Fabio Quartararo")
                .dni("12345678Z")
                 .telefono("600123456")
                .email("eldiablo20.owner@test.com")
                 .tipo(LeadTipo.PROPIETARIO)
                .asignadoAgente(agent)
                .build());

        extraRepository.save(Extra.builder() .nombre("garage").build());

        String propertyPayload = """
                {
                  "precio": 250000,
                  "ciudad": "Madrid",
                  "direccion": "Calle Mayor 10",
                  "codigoPostal": "28001",
                  "habitaciones": 3,
                  "banios": 2,
                  "metrosConstruidos": 120,
                  "metrosUtiles": 100,
                  "propiedadTipo": "PISO",
                  "operacionTipo": "VENTA",
                  "estado": "PUBLICADA",
                  "propietarioClienteId": %d,
                  "propietarioLeadId": %d,
                  "agenteAsignadoId": %d,
                  "extras": ["garage"]
                }
                """.formatted(owner.getId(), lead.getId(), agent.getId());

        mockMvc.perform(post("/api/propiedades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(propertyPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ciudad").value("Madrid"))
                .andExpect(jsonPath("$.extras[0]").value("garage"));

        mockMvc.perform(get("/api/propiedades/search")
                        .param("ciudad", "Madrid")
                        .param("minPrice", "200000")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortField", "precio")
                        .param("sortDirection", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].ciudad").value("Madrid"))
                .andExpect(jsonPath("$.content[0].precio").value(250000.0));
    }

    @Test
    void propertyCreationAutoCreatesMissingFeatures() throws Exception {
        Usuario agent = usuarioRepository.save(Usuario.builder()
                 .nombre("Marc Gestor")
                .email("marc.gestor@test.com")
                .password("password123")
                .role(Role.AGENTE)
                .build());

        Lead lead = leadRepository.save(Lead.builder()
                 .nombre("Marco Owner")
                 .telefono("600222333")
                .email("marco.owner@test.com")
                 .tipo(LeadTipo.PROPIETARIO)
                 .estado(LeadEstado.NUEVO)
                 .descripcion("Propietario")
                .build());

        Cliente owner = clienteRepository.save(Cliente.builder()
                .lead(lead)
                 .nombre("Marco Owner")
                .dni("87654321X")
                 .telefono("600222333")
                .email("marco.owner@test.com")
                 .tipo(LeadTipo.PROPIETARIO)
                .asignadoAgente(agent)
                .build());

        String propertyPayload = """
                {
                  "precio": 325000,
                  "ciudad": "Madrid",
                  "direccion": "Calle Alcala 25",
                  "codigoPostal": "28014",
                  "habitaciones": 4,
                  "banios": 2,
                  "metrosConstruidos": 140,
                  "metrosUtiles": 120,
                  "propiedadTipo": "PISO",
                  "operacionTipo": "VENTA",
                  "estado": "PUBLICADA",
                  "propietarioClienteId": %d,
                  "propietarioLeadId": %d,
                  "agenteAsignadoId": %d,
                  "extras": ["TERRAZA", "ASCENSOR"]
                }
                """.formatted(owner.getId(), lead.getId(), agent.getId());

        mockMvc.perform(post("/api/propiedades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(propertyPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.extras[0]").exists());

        extraRepository.findByNombre("TERRAZA").orElseThrow();
        extraRepository.findByNombre("ASCENSOR").orElseThrow();
    }

    @Test
    void propertyCreationAllowsLeadOwner() throws Exception {
        Usuario agent = usuarioRepository.save(Usuario.builder()
                 .nombre("Ana Gestora")
                .email("ana.propiedad@test.com")
                .password("password123")
                .role(Role.AGENTE)
                .build());

        Lead leadOwner = leadRepository.save(Lead.builder()
                 .nombre("Laura Owner")
                 .telefono("611000222")
                .email("laura.owner@test.com")
                 .tipo(LeadTipo.PROPIETARIO)
                 .estado(LeadEstado.NUEVO)
                 .descripcion("Lead propietario")
                .build());

        String propertyPayload = """
                {
                  "precio": 410000,
                  "ciudad": "Madrid",
                  "direccion": "Calle Serrano 15",
                  "codigoPostal": "28001",
                  "habitaciones": 4,
                  "banios": 2,
                  "metrosConstruidos": 150,
                  "metrosUtiles": 130,
                  "propiedadTipo": "PISO",
                  "operacionTipo": "VENTA",
                  "estado": "CAPTURADA",
                  "propietarioLeadId": %d,
                  "agenteAsignadoId": %d
                }
                """.formatted(leadOwner.getId(), agent.getId());

        mockMvc.perform(post("/api/propiedades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(propertyPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.propietario").doesNotExist())
                .andExpect(jsonPath("$.leadPropietario.id").value(leadOwner.getId()))
                .andExpect(jsonPath("$.leadPropietario.nombre").value("Laura Owner"));
    }
}





