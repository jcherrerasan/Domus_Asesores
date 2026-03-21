package com.crm.crm_domus.service;

import com.crm.crm_domus.dto.request.UsuarioRequest;
import com.crm.crm_domus.exception.ResourceNotFoundException;
import com.crm.crm_domus.model.Usuario;
import com.crm.crm_domus.model.enums.Role;
import com.crm.crm_domus.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void createRejectsDuplicatedEmail() {
        UsuarioRequest request = buildRequest();
        when(usuarioRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(Usuario.builder().id(7L).build()));

        assertThatThrownBy(() -> usuarioService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("correo");

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void updateUsuarioPersistsChangesWhenEmailBelongsToSameUser() {
        UsuarioRequest request = buildRequest();
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nombre("Nombre inicial")
                .email("previo@test.com")
                .password("password123")
                .role(Role.AGENTE)
                .activo(true)
                .build();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = usuarioService.updateUsuario(1L, request);

        assertThat(response.getNombre()).isEqualTo("Laura CRM");
        assertThat(response.getEmail()).isEqualTo("laura@test.com");
        assertThat(response.getRole()).isEqualTo(Role.COORDINADOR);
    }

    @Test
    void getUsuarioByIdThrowsWhenMissing() {
        when(usuarioRepository.findById(44L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.getUsuarioById(44L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("44");
    }

    private UsuarioRequest buildRequest() {
        UsuarioRequest request = new UsuarioRequest();
        request.setNombre("Laura CRM");
        request.setEmail("laura@test.com");
        request.setPassword("password123");
        request.setRole(Role.COORDINADOR);
        return request;
    }
}
