package com.crm.crm_domus.dto.request;

import com.crm.crm_domus.model.enums.LeadTipo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Datos editables de un cliente")
public class ClienteUpdateRequest {

    @Schema(description = "Nombre completo del cliente", example = "Carlos Lopez")
    @NotBlank
    private String nombre;

    @Schema(description = "Documento de identidad", example = "12345678A")
    @NotBlank
    private String dni;

    @Schema(description = "Numero de telefono", example = "600123456")
    @NotBlank
    private String telefono;

    @Schema(description = "Correo electronico", example = "carlos@email.com")
    @Email
    @NotBlank
    private String email;

    @Schema(description = "Tipo de cliente", example = "PROPIETARIO")
    @NotNull
    private LeadTipo tipo;

    @Schema(description = "Direccion del cliente", example = "Calle Alcala 25, Madrid")
    @NotBlank
    private String direccion;

    @Schema(description = "Descripcion del cliente", example = "Cliente verificado")
    private String descripcion;
}
