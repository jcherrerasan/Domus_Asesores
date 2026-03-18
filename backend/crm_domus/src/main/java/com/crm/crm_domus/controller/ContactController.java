package com.crm.crm_domus.controller;

import com.crm.crm_domus.dto.response.ContactSearchResponse;
import com.crm.crm_domus.service.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Contactos", description = "Busqueda global de contactos")
@RestController
@RequestMapping("/api/contactos")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @Operation(summary = "Buscar contactos")
    @GetMapping("/search")
    public List<ContactSearchResponse> buscarContactos(@RequestParam String consulta) {
        return contactService.search(consulta);
    }
}
