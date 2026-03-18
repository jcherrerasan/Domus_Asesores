package com.crm.crm_domus.dto.response;

import com.crm.crm_domus.model.enums.OperacionTipo;
import com.crm.crm_domus.model.enums.PropiedadEstado;
import com.crm.crm_domus.model.enums.PropiedadTipo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class PropiedadResponse {

    @Schema(description = "Id de la propiedad")
    private Long id;

    @Schema(description = "Precio de la propiedad")
    private Double precio;

    @Schema(description = "Indica si tiene exclusiva")
    private Boolean exclusiva;

    @Schema(description = "Numero de habitaciones")
    private Integer habitaciones;

    @Schema(description = "Numero de banos")
    private Integer banios;

    @Schema(description = "Direccion de la propiedad")
    private String direccion;

    @Schema(description = "Ciudad")
    private String ciudad;

    @Schema(description = "Provincia")
    private String provincia;

    @Schema(description = "Superficie construida en metros cuadrados")
    private Double metrosConstruidos;

    @Schema(description = "Superficie util en metros cuadrados")
    private Double metrosUtiles;

    @Schema(description = "Planta")
    private Integer planta;

    @Schema(description = "Ano de construccion")
    private Integer anioConstruccion;

    @Schema(description = "Codigo postal")
    private String codigoPostal;

    @Schema(description = "Descripcion")
    private String descripcion;

    @Schema(description = "Tipo de operacion")
    private OperacionTipo operacionTipo;

    @Schema(description = "Estado de la operacion")
    private PropiedadEstado estado;

    @Schema(description = "Tipo de propiedad")
    private PropiedadTipo propiedadTipo;

    @Schema(description = "Cliente propietario, si existe")
    private ClienteResponse propietario;

    @Schema(description = "Lead propietario")
    private LeadResponse leadPropietario;

    @Schema(description = "Agente asignado")
    private UsuarioResponse asignadoAgente;

    @Schema(description = "Extras asociados")
    private Set<String> extras;
}
