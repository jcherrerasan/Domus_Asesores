package com.crm.crm_domus.dto.request;

import com.crm.crm_domus.model.enums.ValoracionTipo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Datos necesarios para registrar una valoracion de propiedad")
public class ValoracionRequest {

    @Schema(description = "Tipo de valoracion", example = "AUTOMATICA")
    @NotNull
    private ValoracionTipo tipo;

    @Schema(description = "Valor estimado automatico", example = "230000")
    private Double valorEstimadoAutomatico;

    @Schema(description = "Valor estimado real tras visita del agente", example = "250000")
    private Double valorRealEstimado;

    @Schema(description = "Identificador de la propiedad valorada", example = "1")
    @NotNull
    private Long propiedadId;

    @Schema(description = "Identificador del agente que realizo la valoracion", example = "2")
    private Long agenteId;
}
