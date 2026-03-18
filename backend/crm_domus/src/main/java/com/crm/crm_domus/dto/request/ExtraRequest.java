package com.crm.crm_domus.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Datos necesarios para crear o actualizar un extra")
public class ExtraRequest {

    @Schema(description = "Nombre del extra", example = "GARAJE")
    @NotBlank
    private String nombre;
}


