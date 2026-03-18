package com.crm.crm_domus.repository;

import com.crm.crm_domus.model.Valoracion;
import com.crm.crm_domus.model.enums.ValoracionTipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ValoracionRepository extends JpaRepository<Valoracion, Long> {

    List<Valoracion> findByPropiedadId(Long propiedadId);

    List<Valoracion> findByTipo(ValoracionTipo tipo);
}
