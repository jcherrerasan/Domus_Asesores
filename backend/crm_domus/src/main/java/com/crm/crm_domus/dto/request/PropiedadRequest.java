package com.crm.crm_domus.dto.request;

import com.crm.crm_domus.model.enums.OperacionTipo;
import com.crm.crm_domus.model.enums.PropiedadEstado;
import com.crm.crm_domus.model.enums.PropiedadTipo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Datos necesarios para registrar una propiedad inmobiliaria")
public class PropiedadRequest {

    @Schema(description = "Precio de la propiedad", example = "250000")
    @NotNull
    private Double precio;

    @Schema(description = "Ciudad donde se encuentra la propiedad", example = "Madrid")
    private String ciudad;

    @Schema(description = "Direccion de la propiedad", example = "Calle Mayor 10")
    private String direccion;

    @Schema(description = "Codigo postal", example = "28001")
    private String codigoPostal;

    @Schema(description = "Numero de habitaciones", example = "3")
    private Integer habitaciones;

    @Schema(description = "Numero de banos", example = "2")
    private Integer banios;

    @Schema(description = "Planta")
    private Integer planta;

    @Schema(description = "Superficie construida en metros cuadrados", example = "120")
    private Double metrosConstruidos;

    @Schema(description = "Superficie util en metros cuadrados", example = "100")
    private Double metrosUtiles;

    @Schema(description = "Ano de construccion")
    private Integer anioConstruccion;

    @Schema(description = "Tipo de propiedad", example = "PISO")
    @NotNull
    private PropiedadTipo propiedadTipo;

    @Schema(description = "Tipo de operacion", example = "VENTA")
    @NotNull
    private OperacionTipo operacionTipo;

    @Schema(description = "Estado de la operacion", example = "PUBLICADA")
    @NotNull
    private PropiedadEstado estado;

    @Schema(description = "Identificador del propietario si ya es cliente", example = "1")
    private Long propietarioClienteId;

    @Schema(description = "Identificador del lead del propietario", example = "2")
    @NotNull
    private Long propietarioLeadId;

    @Schema(description = "Identificador del agente asignado", example = "2")
    @NotNull
    private Long agenteAsignadoId;

    @Schema(description = "Provincia")
    private String provincia;

    @Schema(description = "Descripcion de la propiedad")
    private String descripcion;

    @Schema(description = "Si tiene exclusiva")
    private Boolean exclusiva;

    @Schema(description = "Extras de la propiedad", example = "[\"GARAJE\",\"TERRAZA\"]")
    private List<String> extras;
}
