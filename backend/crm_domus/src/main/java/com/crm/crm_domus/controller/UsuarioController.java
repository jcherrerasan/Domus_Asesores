package com.crm.crm_domus.controller;

import com.crm.crm_domus.dto.request.UsuarioRequest;
import com.crm.crm_domus.dto.response.UsuarioResponse;
import com.crm.crm_domus.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Usuarios", description = "Gestion de usuarios del CRM")
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Operation(summary = "Crear un usuario", description = "Registra un nuevo usuario del CRM con rol asignado")
    @PostMapping
    public UsuarioResponse createUsuario(@Valid @RequestBody UsuarioRequest usuario) {
        return usuarioService.create(usuario);
    }

    @Operation(summary = "Obtener todos los usuarios")
    @GetMapping
    public List<UsuarioResponse> getAllUsuarios() {
        return usuarioService.getAllUsuarios();
    }

    @Operation(summary = "Obtener usuario por id")
    @GetMapping("/{id}")
    public UsuarioResponse getUsuarioById(@PathVariable Long id) {
        return usuarioService.getUsuarioById(id);
    }

    @Operation(summary = "Actualizar usuario por id")
    @PutMapping("/{id}")
    public UsuarioResponse updateUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioRequest usuario) {
        return usuarioService.updateUsuario(id, usuario);
    }

    @DeleteMapping("/{id}")
    public void deleteUsuario(@PathVariable Long id) {
        usuarioService.deleteUsuario(id);
    }
}


