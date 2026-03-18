package com.crm.crm_domus.controller;

import com.crm.crm_domus.dto.request.ClienteRequest;
import com.crm.crm_domus.dto.request.ClienteUpdateRequest;
import com.crm.crm_domus.dto.response.ClienteResponse;
import com.crm.crm_domus.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Clientes", description = "Gestion de clientes verificados")
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @Operation(summary = "Crear un nuevo cliente", description = "Convierte un lead en cliente verificado")
    @PostMapping
    public ClienteResponse createCliente(@Valid @RequestBody ClienteRequest cliente) {
        return clienteService.create(cliente);
    }

    @Operation(summary = "Obtener todos los clientes")
    @GetMapping
    public List<ClienteResponse> getAllClientes() {
        return clienteService.getAllClientes();
    }

    @Operation(summary = "Buscar cliente por id")
    @GetMapping("/{id}")
    public ClienteResponse getClienteById(@PathVariable Long id) {
        return clienteService.getClienteById(id);
    }

    @Operation(summary = "Actualizar cliente por id")
    @PutMapping("/{id}")
    public ClienteResponse updateCliente(@PathVariable Long id, @Valid @RequestBody ClienteUpdateRequest cliente) {
        return clienteService.updateCliente(id, cliente);
    }

    @Operation(summary = "Eliminar cliente por id")
    @DeleteMapping("/{id}")
    public void deleteCliente(@PathVariable Long id) {
        clienteService.deleteCliente(id);
    }

    @Operation(summary = "Buscar cliente por nombre, telefono y email")
    @GetMapping("/search")
    public List<ClienteResponse> searchClientes(

            @Parameter(description = "Nombre del cliente")
            @RequestParam(required = false) String nombre,

            @Parameter(description = "Telefono del cliente")
            @RequestParam(required = false) String telefono,

            @Parameter(description = "Email del cliente")
            @RequestParam(required = false) String email

    ) {
        return clienteService.searchClientes(nombre, telefono, email);
    }
}


