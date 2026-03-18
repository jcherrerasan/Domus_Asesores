package com.crm.crm_domus.model;

import com.crm.crm_domus.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nombre es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @Email(message = "Email debe ser válido")
    @NotBlank(message = "Email es obligatorio")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password es obligatorio")
    @Size(min = 8, message = "Password debe ser de al menos 8 caracteres")
    @Column(nullable = false)
    private String password;

    @NotNull(message = "Rol es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder.Default
    private Boolean borrado = false;

    private LocalDateTime borradoDesde;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

