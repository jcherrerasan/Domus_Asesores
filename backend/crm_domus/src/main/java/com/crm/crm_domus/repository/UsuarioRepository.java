package com.crm.crm_domus.repository;

import com.crm.crm_domus.model.Usuario;
import com.crm.crm_domus.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByRole(Role role);

}


