package com.crm.crm_domus.service;

import com.crm.crm_domus.dto.request.ExtraRequest;
import com.crm.crm_domus.exception.ResourceNotFoundException;
import com.crm.crm_domus.model.Extra;
import com.crm.crm_domus.repository.ExtraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExtraService {

    private final ExtraRepository extraRepository;

    public Extra create(ExtraRequest request) {
        return extraRepository.save(Extra.builder().nombre(request.getNombre()).build());
    }

    public List<Extra> getAll() {
        return extraRepository.findAll();
    }

    public Extra getById(Long id) {
        return extraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Extra no encontrado con id: " + id));
    }

    public Extra update(Long id, ExtraRequest request) {
        Extra extra = getById(id);
        extra.setNombre(request.getNombre());
        return extraRepository.save(extra);
    }

    public void delete(Long id) {
        extraRepository.delete(getById(id));
    }
}
