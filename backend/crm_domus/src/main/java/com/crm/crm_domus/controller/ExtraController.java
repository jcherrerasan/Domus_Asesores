package com.crm.crm_domus.controller;

import com.crm.crm_domus.dto.request.ExtraRequest;
import com.crm.crm_domus.model.Extra;
import com.crm.crm_domus.service.ExtraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Extras", description = "Gestion de extras de propiedades")
@RestController
@RequestMapping("/api/extras")
@RequiredArgsConstructor
public class ExtraController {

    private final ExtraService extraService;

    @Operation(summary = "Crear un extra")
    @PostMapping
    public Extra create(@Valid @RequestBody ExtraRequest extra) {
        return extraService.create(extra);
    }

    @Operation(summary = "Obtener todos los extras")
    @GetMapping
    public List<Extra> getAll() {
        return extraService.getAll();
    }

    @Operation(summary = "Obtener un extra por id")
    @GetMapping("/{id}")
    public Extra getById(@PathVariable Long id) {
        return extraService.getById(id);
    }

    @Operation(summary = "Actualizar un extra por id")
    @PutMapping("/{id}")
    public Extra update(@PathVariable Long id, @Valid @RequestBody ExtraRequest extra) {
        return extraService.update(id, extra);
    }

    @Operation(summary = "Eliminar un extra por id")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        extraService.delete(id);
    }
}
