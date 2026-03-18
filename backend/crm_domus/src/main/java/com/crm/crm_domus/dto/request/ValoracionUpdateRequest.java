package com.crm.crm_domus.dto.request;

import com.crm.crm_domus.model.enums.ValoracionTipo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Datos editables de una valoracion")
public class ValoracionUpdateRequest {

    @Schema(description = "Tipo de valoracion", example = "AUTOMATICA")
    @NotNull
    private ValoracionTipo tipo;

    @Schema(description = "Valor estimado automatico", example = "230000")
    private Double valorEstimadoAutomatico;

    @Schema(description = "Valor estimado real tras visita del agente", example = "250000")
    private Double valorRealEstimado;
}
