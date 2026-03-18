package com.crm.crm_domus.repository;

import com.crm.crm_domus.model.Lead;
import com.crm.crm_domus.model.enums.LeadEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LeadRepository extends JpaRepository<Lead, Long> {

    List<Lead> findByEstado(LeadEstado estado);

    List<Lead> findByAsignadoAId(Long usuarioId);

    @Query("""
    SELECT l FROM Lead l
    WHERE (:nombre IS NULL OR LOWER(l.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
    AND (:telefono IS NULL OR l.telefono LIKE CONCAT('%', :telefono, '%'))
    AND (:email IS NULL OR LOWER(l.email) LIKE LOWER(CONCAT('%', :email, '%')))
    """)
    List<Lead> searchLeads(
            String nombre,
            String telefono,
            String email
    );
}
