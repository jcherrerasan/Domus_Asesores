package com.crm.crm_domus.controller;

import com.crm.crm_domus.dto.request.LeadCreateRequest;
import com.crm.crm_domus.dto.request.LeadRequest;
import com.crm.crm_domus.dto.response.LeadResponse;
import com.crm.crm_domus.service.LeadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Leads", description = "Gestion de leads")
@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
public class LeadController {

    private final LeadService leadService;

    @Operation(summary = "Crear un nuevo lead", description = "Registra un nuevo lead en el sistema")
    @PostMapping
    public LeadResponse createLead(@Valid @RequestBody LeadCreateRequest lead) {
        return leadService.create(lead);
    }

    @Operation(summary = "Obtener todos los leads")
    @GetMapping
    public List<LeadResponse> getAllLeads() {
        return leadService.getAllLeads();
    }

    @Operation(summary = "Obtener lead por id")
    @GetMapping("/{id}")
    public LeadResponse getLeadById(@PathVariable Long id) {
        return leadService.getLeadById(id);
    }

    @Operation(summary = "Actualizar lead por id")
    @PutMapping("/{id}")
    public LeadResponse updateLead(@PathVariable Long id, @Valid @RequestBody LeadRequest lead) {
        return leadService.updateLead(id, lead);
    }

    @Operation(summary = "Eliminar lead por id")
    @DeleteMapping("/{id}")
    public void deleteLead(@PathVariable Long id) {
        leadService.deleteLead(id);
    }

    @Operation(summary = "Buscar leads por nombre, telefono y correo")
    @GetMapping("/search")
    public List<LeadResponse> searchLeads(
            @Parameter(description = "Nombre del lead")
            @RequestParam(required = false) String nombre,
            @Parameter(description = "Telefono del lead")
            @RequestParam(required = false) String telefono,
            @Parameter(description = "Correo del lead")
            @RequestParam(required = false) String email
    ) {
        return leadService.searchLeads(nombre, telefono, email);
    }
}
