package com.crm.crm_domus.repository;

import com.crm.crm_domus.model.Cliente;
import com.crm.crm_domus.model.enums.LeadTipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByLeadId(Long leadId);

    List<Cliente> findByTipo(LeadTipo tipo);

    List<Cliente> findByAsignadoAgenteId(Long agenteId);

    @Query("""
    SELECT c FROM Cliente c
    WHERE (:nombre IS NULL OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
    AND (:telefono IS NULL OR c.telefono LIKE CONCAT('%', :telefono, '%'))
    AND (:email IS NULL OR LOWER(c.email) LIKE LOWER(CONCAT('%', :email, '%')))
    """)
    List<Cliente> searchClientes(
            String nombre,
            String telefono,
            String email
    );

    Optional<Cliente> findByDni(String dni);
}
