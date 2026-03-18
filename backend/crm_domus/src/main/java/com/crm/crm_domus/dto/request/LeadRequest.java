package com.crm.crm_domus.dto.request;

import com.crm.crm_domus.model.enums.LeadEstado;
import com.crm.crm_domus.model.enums.LeadTipo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Datos necesarios para registrar un lead")
public class LeadRequest {

    @Schema(description = "Nombre del lead", example = "Carlos Lopez")
    @NotBlank
    private String nombre;

    @Schema(description = "Numero de telefono", example = "600123456")
    @NotBlank
    private String telefono;

    @Schema(description = "Correo electronico del lead", example = "carlos@email.com")
    @Email
    private String email;

    @Schema(description = "Tipo de lead", example = "PROPIETARIO")
    @NotNull
    private LeadTipo tipo;

    @Schema(description = "Estado en el que se encuentra")
    private LeadEstado estado;

    @Schema(description = "Descripcion sobre el lead", example = "Interesado en vender su vivienda")
    private String descripcion;
}
