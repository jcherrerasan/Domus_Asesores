package com.crm.crm_domus.controller;

import com.crm.crm_domus.dto.request.PropiedadRequest;
import com.crm.crm_domus.dto.request.PropiedadUpdateRequest;
import com.crm.crm_domus.dto.response.PropiedadResponse;
import com.crm.crm_domus.dto.response.ValoracionResponse;
import com.crm.crm_domus.model.Extra;
import com.crm.crm_domus.model.Propiedad;
import com.crm.crm_domus.model.enums.OperacionTipo;
import com.crm.crm_domus.model.enums.PropiedadTipo;
import com.crm.crm_domus.service.PropiedadService;
import com.crm.crm_domus.service.ValoracionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Tag(name = "Propiedades", description = "Gestion de propiedades inmobiliarias")
@RestController
@RequestMapping("/api/propiedades")
@RequiredArgsConstructor
public class PropiedadController {

    private final PropiedadService propiedadService;
    private final ValoracionService valoracionService;

    @Operation(summary = "Crear nueva propiedad", description = "Registra una nueva propiedad inmobiliaria")
    @PostMapping
    public PropiedadResponse createPropiedad(@Valid @RequestBody PropiedadRequest propiedad) {
        return propiedadService.create(propiedad);
    }

    @Operation(summary = "Obtener todas las propiedades")
    @GetMapping
    public List<PropiedadResponse> getAllPropiedades() {
        return propiedadService.getAllPropiedades();
    }

    @Operation(summary = "Obtener una propiedad por id")
    @GetMapping("/{id}")
    public PropiedadResponse getPropiedadById(@PathVariable Long id) {
        return propiedadService.getPropiedadById(id);
    }

    @Operation(summary = "Actualizar una propiedad por id")
    @PutMapping("/{id}")
    public PropiedadResponse updatePropiedad(@PathVariable Long id, @Valid @RequestBody PropiedadUpdateRequest propiedad) {
        return propiedadService.updatePropiedad(id, propiedad);
    }

    @Operation(summary = "Eliminar una propiedad por id")
    @DeleteMapping("/{id}")
    public void deletePropiedad(@PathVariable Long id) {
        propiedadService.deletePropiedad(id);
    }

    @Operation(summary = "Obtener las valoraciones de una propiedad")
    @GetMapping("/{id}/valoraciones")
    public List<ValoracionResponse> getPropiedadValoraciones(@PathVariable Long id) {
        return valoracionService.getByProperty(id);
    }

    @Operation(summary = "Obtener los extras de una propiedad")
    @GetMapping("/{id}/extras")
    public Set<String> getPropiedadExtras(@PathVariable Long id) {
        Propiedad propiedad = propiedadService.getEntityById(id);
        return propiedad.getExtras().stream().map(Extra::getNombre).collect(Collectors.toSet());
    }

    @Operation(summary = "Buscar propiedades con filtros")
    @GetMapping("/search")
    public Page<PropiedadResponse> searchPropiedades(
            @Parameter(description = "Ciudad de la propiedad") @RequestParam(required = false) String ciudad,
            @Parameter(description = "Precio minimo") @RequestParam(required = false) Double precioMinimo,
            @Parameter(description = "Precio máximo") @RequestParam(required = false) Double precioMaximo,
            @Parameter(description = "Numero de habitaciones") @RequestParam(required = false) Integer habitaciones,
            @Parameter(description = "Numero de baños") @RequestParam(required = false) Integer banios,
            @Parameter(description = "Superficie construida minima") @RequestParam(required = false) Double metrosConstruidosMinimos,
            @Parameter(description = "Tipo de propiedad") @RequestParam(required = false) PropiedadTipo propiedadTipo,
            @Parameter(description = "Tipo de operación") @RequestParam(required = false) OperacionTipo operacionTipo,
            @Parameter(description = "Extras que debe tener la propiedad") @RequestParam(required = false) List<String> extras,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo por el que se ordena") @RequestParam(defaultValue = "precio") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        return propiedadService.searchPropiedades(
                ciudad,
                precioMinimo,
                precioMaximo,
                habitaciones,
                banios,
                metrosConstruidosMinimos,
                propiedadTipo,
                operacionTipo,
                extras,
                page,
                size,
                sortField,
                sortDirection
        );
    }

    @Operation(summary = "Busqueda global de propiedades")
    @GetMapping("/search-global")
    public List<PropiedadResponse> searchGlobal(@RequestParam String query) {
        return propiedadService.searchGlobal(query);
    }
}
