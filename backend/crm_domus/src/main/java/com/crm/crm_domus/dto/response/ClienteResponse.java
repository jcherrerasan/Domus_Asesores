package com.crm.crm_domus.dto.response;

import com.crm.crm_domus.model.enums.LeadTipo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClienteResponse {

    @Schema(description = "Id del cliente")
    private Long id;

    @Schema(description = "Id del lead asociado")
    private Long leadId;

    @Schema(description = "Nombre del cliente")
    private String nombre;

    @Schema(description = "DNI del cliente")
    private String dni;

    @Schema(description = "Numero de telefono")
    private String telefono;

    @Schema(description = "Correo electronico")
    private String email;

    @Schema(description = "Tipo de cliente")
    private LeadTipo tipo;

    @Schema(description = "Direccion del cliente")
    private String direccion;

    @Schema(description = "Descripcion heredada del lead")
    private String descripcion;
}
