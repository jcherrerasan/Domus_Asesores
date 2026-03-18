package com.crm.crm_domus.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContactSearchResponse {

    private String tipo;
    private Long id;
    private String nombre;
    private String telefono;
    private String email;
}
