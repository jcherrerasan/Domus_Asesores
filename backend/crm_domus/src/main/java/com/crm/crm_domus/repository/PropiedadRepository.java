package com.crm.crm_domus.repository;

import com.crm.crm_domus.model.Propiedad;
import com.crm.crm_domus.model.enums.OperacionTipo;
import com.crm.crm_domus.model.enums.PropiedadEstado;
import com.crm.crm_domus.model.enums.PropiedadTipo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PropiedadRepository extends JpaRepository<Propiedad, Long> {

    List<Propiedad> findByAsignadoAgenteId(Long agenteId);

    List<Propiedad> findByEstado(PropiedadEstado estado);

    @Query("""
    SELECT DISTINCT p FROM Propiedad p
    LEFT JOIN p.extras e
    WHERE (:ciudad IS NULL OR LOWER(p.ciudad) = LOWER(:ciudad))
    AND (:precioMinimo IS NULL OR p.precio >= :precioMinimo)
    AND (:precioMaximo IS NULL OR p.precio <= :precioMaximo)
    AND (:habitaciones IS NULL OR p.habitaciones >= :habitaciones)
    AND (:banios IS NULL OR p.banios >= :banios)
    AND (:metrosConstruidosMinimos IS NULL OR p.metrosConstruidos >= :metrosConstruidosMinimos)
    AND (:propiedadTipo IS NULL OR p.propiedadTipo = :propiedadTipo)
    AND (:operacionTipo IS NULL OR p.operacionTipo = :operacionTipo)
    AND (:extras IS NULL OR LOWER(e.nombre) IN :extras)
    """)
    Page<Propiedad> searchPropiedades(
            String ciudad,
            Double precioMinimo,
            Double precioMaximo,
            Integer habitaciones,
            Integer banios,
            Double metrosConstruidosMinimos,
            PropiedadTipo propiedadTipo,
            OperacionTipo operacionTipo,
            List<String> extras,
            Pageable pageable
    );

    @Query("""
    SELECT p FROM Propiedad p
    WHERE LOWER(p.ciudad) LIKE LOWER(CONCAT('%', :query, '%'))
    OR LOWER(p.direccion) LIKE LOWER(CONCAT('%', :query, '%'))
    OR LOWER(p.codigoPostal) LIKE LOWER(CONCAT('%', :query, '%'))
    OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :query, '%'))
    """)
    List<Propiedad> searchGlobal(String query);
}
