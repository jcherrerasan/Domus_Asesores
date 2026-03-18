package com.crm.crm_domus.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class DashboardResponse {

    @Schema(description = "Número total de leads")
    private long totalLeads;

    @Schema(description = "Número de clientes")
    private long totalAccounts;

    @Schema(description = "Número de propiedades")
    private long totalProperties;

    @Schema(description = "Precio medio")
    private Double averagePropertyPrice;

}
