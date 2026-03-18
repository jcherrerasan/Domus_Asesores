package com.crm.crm_domus.model;

import com.crm.crm_domus.model.enums.LeadTipo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lead_id")
    private Lead lead;

    @NotBlank(message = "Nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "DNI es obligatorio")
    private String dni;

    @NotBlank(message = "Telefono es obligatorio")
    private String telefono;

    @Email(message = "Email debe ser valido")
    private String email;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Tipo de cliente es obligatorio")
    private LeadTipo tipo;

    private String direccion;

    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "asignado_agente_id")
    private Usuario asignadoAgente;

    @Column(name = "creado_en", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
