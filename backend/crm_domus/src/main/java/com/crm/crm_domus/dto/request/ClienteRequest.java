package com.crm.crm_domus.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Datos necesarios para convertir un lead existente en cliente")
public class ClienteRequest {

    @Schema(description = "Identificador del lead previo", example = "1")
    @NotNull
    private Long leadId;

    @Schema(description = "Documento de identidad", example = "12345678A")
    @NotBlank
    private String dni;

    @Schema(description = "Direccion del cliente", example = "Calle Alcala 25, Madrid")
    @NotBlank
    private String direccion;
}
