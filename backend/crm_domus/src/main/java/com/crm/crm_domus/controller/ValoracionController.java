package com.crm.crm_domus.controller;

import com.crm.crm_domus.dto.request.ValoracionRequest;
import com.crm.crm_domus.dto.request.ValoracionUpdateRequest;
import com.crm.crm_domus.dto.response.ValoracionResponse;
import com.crm.crm_domus.service.ValoracionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Valoraciones", description = "Gestion de valoraciones de propiedades")
@RestController
@RequestMapping("/api/valoraciones")
@RequiredArgsConstructor
public class ValoracionController {

    private final ValoracionService service;

    @Operation(summary = "Crear una valoracion")
    @PostMapping
    public ValoracionResponse create(@Valid @RequestBody ValoracionRequest valoracion) {
        return service.create(valoracion);
    }

    @Operation(summary = "Obtener todas las valoraciones")
    @GetMapping
    public List<ValoracionResponse> getAll() {
        return service.getAll();
    }

    @Operation(summary = "Obtener una valoracion por id")
    @GetMapping("/{id}")
    public ValoracionResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @Operation(summary = "Actualizar una valoracion por id")
    @PutMapping("/{id}")
    public ValoracionResponse update(@PathVariable Long id, @Valid @RequestBody ValoracionUpdateRequest valoracion) {
        return service.update(id, valoracion);
    }

    @Operation(summary = "Eliminar una valoracion por id")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @Operation(summary = "Obtener valoraciones por propiedad")
    @GetMapping("/propiedad/{propiedadId}")
    public List<ValoracionResponse> getByProperty(@PathVariable Long propiedadId) {
        return service.getByProperty(propiedadId);
    }
}

