package com.crm.crm_domus.dto.request;

import com.crm.crm_domus.model.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Datos necesarios para crear o registrar un usuario del CRM")
public class UsuarioRequest {

    @Schema(description = "Nombre completo del usuario", example = "Juan Perez")
    @NotBlank
    private String nombre;

    @Schema(description = "Correo electronico del usuario", example = "juan@email.com")
    @Email
    @NotBlank
    private String email;

    @Schema(description = "Contrasena del usuario", example = "password123")
    @NotBlank
    private String password;

    @Schema(description = "Rol del usuario", example = "AGENTE")
    @NotNull
    private Role role;
}
