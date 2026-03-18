package com.crm.crm_domus.service;

import com.crm.crm_domus.dto.response.DashboardResponse;
import com.crm.crm_domus.repository.ClienteRepository;
import com.crm.crm_domus.repository.LeadRepository;
import com.crm.crm_domus.repository.PropiedadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final LeadRepository leadRepository;
    private final ClienteRepository clienteRepository;
    private final PropiedadRepository propiedadRepository;

    public DashboardResponse getDashboard() {
        long leads = leadRepository.count();
        long clientes = clienteRepository.count();
        long propiedades = propiedadRepository.count();

        Double precioMedio = propiedadRepository.findAll()
                .stream()
                .mapToDouble(propiedad -> propiedad.getPrecio())
                .average()
                .orElse(0);

        return DashboardResponse.builder()
                .totalLeads(leads)
                .totalAccounts(clientes)
                .totalProperties(propiedades)
                .averagePropertyPrice(precioMedio)
                .build();
    }
}
