package com.crm.crm_domus.service;

import com.crm.crm_domus.dto.response.ContactSearchResponse;
import com.crm.crm_domus.model.Cliente;
import com.crm.crm_domus.model.Lead;
import com.crm.crm_domus.repository.ClienteRepository;
import com.crm.crm_domus.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final LeadRepository leadRepository;
    private final ClienteRepository clienteRepository;

    public List<ContactSearchResponse> search(String query) {
        List<ContactSearchResponse> resultados = new ArrayList<>();

        for (Lead lead : leadRepository.searchLeads(query, query, query)) {
            resultados.add(ContactSearchResponse.builder()
                    .tipo("LEAD")
                    .id(lead.getId())
                    .nombre(lead.getNombre())
                    .telefono(lead.getTelefono())
                    .email(lead.getEmail())
                    .build());
        }

        for (Cliente cliente : clienteRepository.searchClientes(query, query, query)) {
            resultados.add(ContactSearchResponse.builder()
                    .tipo("CLIENTE")
                    .id(cliente.getId())
                    .nombre(cliente.getNombre())
                    .telefono(cliente.getTelefono())
                    .email(cliente.getEmail())
                    .build());
        }

        return resultados;
    }
}
