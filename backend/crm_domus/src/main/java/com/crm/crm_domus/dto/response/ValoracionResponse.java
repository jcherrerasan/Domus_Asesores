package com.crm.crm_domus.dto.response;

import com.crm.crm_domus.model.enums.ValoracionTipo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ValoracionResponse {

    private Long id;

    @Schema(description = "Tipo de valoracion")
    private ValoracionTipo tipo;

    @Schema(description = "Valor estimado automatico")
    private Double valorEstimadoAutomatico;

    @Schema(description = "Valor estimado real tras visita del agente")
    private Double valorRealEstimado;

    @Schema(description = "URL de la valoracion")
    private String urlValoracion;

    @Schema(description = "Fecha de creacion de la valoracion")
    private LocalDateTime creadoEn;

    @Schema(description = "Identificador de la propiedad valorada")
    private Long propiedadId;

    @Schema(description = "Identificador del agente que realizo la valoracion")
    private Long agenteId;

    @Schema(description = "Nombre del agente que realizo la valoracion")
    private String nombreAgente;
}
