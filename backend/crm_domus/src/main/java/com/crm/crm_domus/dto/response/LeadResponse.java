package com.crm.crm_domus.dto.response;

import com.crm.crm_domus.model.enums.LeadEstado;
import com.crm.crm_domus.model.enums.LeadTipo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeadResponse {

    @Schema(description = "Id del lead")
    private Long id;

    @Schema(description = "Nombre del lead")
    private String nombre;

    @Schema(description = "Numero de telefono")
    private String telefono;

    @Schema(description = "Correo electronico")
    private String email;

    @Schema(description = "Tipo de lead")
    private LeadTipo tipo;

    @Schema(description = "Estado en el que se encuentra")
    private LeadEstado estado;

    @Schema(description = "Descripcion")
    private String descripcion;

    @Schema(description = "Usuario que creo el lead")
    private UsuarioResponse creadoPor;

    @Schema(description = "Usuario asignado al lead")
    private UsuarioResponse asignadoA;
}
