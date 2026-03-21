package com.crm.crm_domus.service;

import com.crm.crm_domus.dto.request.PropiedadRequest;
import com.crm.crm_domus.dto.request.PropiedadUpdateRequest;
import com.crm.crm_domus.dto.response.ClienteResponse;
import com.crm.crm_domus.dto.response.LeadResponse;
import com.crm.crm_domus.dto.response.PropiedadResponse;
import com.crm.crm_domus.dto.response.UsuarioResponse;
import com.crm.crm_domus.exception.ResourceNotFoundException;
import com.crm.crm_domus.model.Cliente;
import com.crm.crm_domus.model.Extra;
import com.crm.crm_domus.model.Lead;
import com.crm.crm_domus.model.Propiedad;
import com.crm.crm_domus.model.Usuario;
import com.crm.crm_domus.model.enums.OperacionTipo;
import com.crm.crm_domus.model.enums.PropiedadTipo;
import com.crm.crm_domus.repository.ClienteRepository;
import com.crm.crm_domus.repository.ExtraRepository;
import com.crm.crm_domus.repository.LeadRepository;
import com.crm.crm_domus.repository.PropiedadRepository;
import com.crm.crm_domus.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PropiedadService {

    private final PropiedadRepository propiedadRepository;
    private final ExtraRepository extraRepository;
    private final ClienteRepository clienteRepository;
    private final LeadRepository leadRepository;
    private final UsuarioRepository usuarioRepository;

    public PropiedadResponse create(PropiedadRequest request) {
        Propiedad propiedad = new Propiedad();
        applyRequest(propiedad, request);
        return mapToDTO(propiedadRepository.save(propiedad));
    }

    public List<PropiedadResponse> getAllPropiedades() {
        return propiedadRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    public PropiedadResponse getPropiedadById(Long id) {
        return mapToDTO(getEntityById(id));
    }

    public PropiedadResponse updatePropiedad(Long id, PropiedadUpdateRequest request) {
        Propiedad propiedad = getEntityById(id);
        applyUpdate(propiedad, request);
        return mapToDTO(propiedadRepository.save(propiedad));
    }

    public void deletePropiedad(Long id) {
        propiedadRepository.delete(getEntityById(id));
    }

    public Propiedad getEntityById(Long id) {
        return propiedadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Propiedad no encontrada con id: " + id));
    }

    public Set<String> getExtrasByPropiedadId(Long id) {
        Propiedad propiedad = getEntityById(id);
        if (propiedad.getExtras() == null) {
            return Set.of();
        }

        return propiedad.getExtras().stream()
                .map(Extra::getNombre)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Page<PropiedadResponse> searchPropiedades(
            String ciudad,
            Double precioMinimo,
            Double precioMaximo,
            Integer habitaciones,
            Integer banios,
            Double metrosConstruidosMinimos,
            PropiedadTipo propiedadTipo,
            OperacionTipo operacionTipo,
            List<String> extras,
            int page,
            int size,
            String sortField,
            String sortDirection
    ) {
        Sort sort = Sort.by(sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortField);
        Pageable pageable = PageRequest.of(page, size, sort);

        return propiedadRepository.searchPropiedades(
                ciudad,
                precioMinimo,
                precioMaximo,
                habitaciones,
                banios,
                metrosConstruidosMinimos,
                propiedadTipo,
                operacionTipo,
                extras,
                pageable
        ).map(this::mapToDTO);
    }

    public List<PropiedadResponse> searchGlobal(String query) {
        return propiedadRepository.searchGlobal(query).stream().map(this::mapToDTO).toList();
    }

    private void applyRequest(Propiedad propiedad, PropiedadRequest request) {
        Usuario agenteAsignado = usuarioRepository.findById(request.getAgenteAsignadoId())
                .orElseThrow(() -> new ResourceNotFoundException("Agente no encontrado con id: " + request.getAgenteAsignadoId()));
        Lead leadPropietario = leadRepository.findById(request.getPropietarioLeadId())
                .orElseThrow(() -> new ResourceNotFoundException("Lead propietario no encontrado con id: " + request.getPropietarioLeadId()));

        propiedad.setPrecio(request.getPrecio());
        propiedad.setCiudad(request.getCiudad());
        propiedad.setDireccion(request.getDireccion());
        propiedad.setCodigoPostal(request.getCodigoPostal());
        propiedad.setHabitaciones(request.getHabitaciones());
        propiedad.setBanios(request.getBanios());
        propiedad.setMetrosConstruidos(request.getMetrosConstruidos());
        propiedad.setMetrosUtiles(request.getMetrosUtiles());
        propiedad.setPlanta(request.getPlanta());
        propiedad.setAnioConstruccion(request.getAnioConstruccion());
        propiedad.setExclusiva(request.getExclusiva());
        propiedad.setProvincia(request.getProvincia());
        propiedad.setDescripcion(request.getDescripcion());
        propiedad.setPropiedadTipo(request.getPropiedadTipo());
        propiedad.setOperacionTipo(request.getOperacionTipo());
        propiedad.setEstado(request.getEstado());
        propiedad.setAsignadoAgente(agenteAsignado);
        propiedad.setLeadPropietario(leadPropietario);

        if (request.getPropietarioClienteId() != null) {
            Cliente propietario = clienteRepository.findById(request.getPropietarioClienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente propietario no encontrado con id: " + request.getPropietarioClienteId()));
            if (propietario.getLead() == null || !propietario.getLead().getId().equals(leadPropietario.getId())) {
                throw new IllegalArgumentException("El cliente propietario debe pertenecer al lead propietario indicado");
            }
            propiedad.setPropietario(propietario);
        } else {
            propiedad.setPropietario(null);
        }

        propiedad.setExtras(resolveExtras(request.getExtras()));
    }

    private void applyUpdate(Propiedad propiedad, PropiedadUpdateRequest request) {
        propiedad.setPrecio(request.getPrecio());
        propiedad.setCiudad(request.getCiudad());
        propiedad.setDireccion(request.getDireccion());
        propiedad.setCodigoPostal(request.getCodigoPostal());
        propiedad.setHabitaciones(request.getHabitaciones());
        propiedad.setBanios(request.getBanios());
        propiedad.setMetrosConstruidos(request.getMetrosConstruidos());
        propiedad.setMetrosUtiles(request.getMetrosUtiles());
        propiedad.setPlanta(request.getPlanta());
        propiedad.setAnioConstruccion(request.getAnioConstruccion());
        propiedad.setExclusiva(request.getExclusiva());
        propiedad.setProvincia(request.getProvincia());
        propiedad.setDescripcion(request.getDescripcion());
        propiedad.setPropiedadTipo(request.getPropiedadTipo());
        propiedad.setOperacionTipo(request.getOperacionTipo());
        propiedad.setEstado(request.getEstado());
        propiedad.setExtras(resolveExtras(request.getExtras()));
    }

    private Set<Extra> resolveExtras(List<String> nombres) {
        if (nombres == null) {
            return null;
        }

        return nombres.stream()
                .map(this::findOrCreateExtra)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Extra findOrCreateExtra(String nombre) {
        String nombreNormalizado = nombre == null ? null : nombre.trim();
        if (nombreNormalizado == null || nombreNormalizado.isBlank()) {
            throw new IllegalArgumentException("El nombre del extra no puede estar vacio");
        }

        return extraRepository.findByNombre(nombreNormalizado)
                .orElseGet(() -> extraRepository.save(Extra.builder().nombre(nombreNormalizado).build()));
    }

    private PropiedadResponse mapToDTO(Propiedad propiedad) {
        return PropiedadResponse.builder()
                .id(propiedad.getId())
                .precio(propiedad.getPrecio())
                .exclusiva(propiedad.getExclusiva())
                .metrosConstruidos(propiedad.getMetrosConstruidos())
                .metrosUtiles(propiedad.getMetrosUtiles())
                .planta(propiedad.getPlanta())
                .anioConstruccion(propiedad.getAnioConstruccion())
                .codigoPostal(propiedad.getCodigoPostal())
                .descripcion(propiedad.getDescripcion())
                .habitaciones(propiedad.getHabitaciones())
                .banios(propiedad.getBanios())
                .direccion(propiedad.getDireccion())
                .ciudad(propiedad.getCiudad())
                .provincia(propiedad.getProvincia())
                .operacionTipo(propiedad.getOperacionTipo())
                .estado(propiedad.getEstado())
                .propiedadTipo(propiedad.getPropiedadTipo())
                .propietario(mapCliente(propiedad.getPropietario()))
                .leadPropietario(mapLead(propiedad.getLeadPropietario()))
                .asignadoAgente(mapUsuario(propiedad.getAsignadoAgente()))
                .extras(propiedad.getExtras() == null ? null :
                        propiedad.getExtras().stream().map(Extra::getNombre).collect(Collectors.toSet()))
                .build();
    }

    private ClienteResponse mapCliente(Cliente cliente) {
        if (cliente == null) {
            return null;
        }

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

    private LeadResponse mapLead(Lead lead) {
        if (lead == null) {
            return null;
        }

        return LeadResponse.builder()
                .id(lead.getId())
                .nombre(lead.getNombre())
                .telefono(lead.getTelefono())
                .email(lead.getEmail())
                .tipo(lead.getTipo())
                .estado(lead.getEstado())
                .descripcion(lead.getDescripcion())
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
}
