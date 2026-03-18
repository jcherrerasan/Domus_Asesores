package com.crm.crm_domus.dto.response;

import com.crm.crm_domus.model.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioResponse {

    @Schema(description = "Id del usuario")
    private Long id;

    @Schema(description = "Nombre del usuario")
    private String nombre;

    @Schema(description = "Correo electronico del usuario")
    private String email;

    @Schema(description = "Rol del usuario")
    private Role role;

    @Schema(description = "Indica si el usuario esta activo")
    private Boolean activo;
}
