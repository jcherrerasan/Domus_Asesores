package com.crm.crm_domus.model;

import com.crm.crm_domus.model.enums.OperacionTipo;
import com.crm.crm_domus.model.enums.PropiedadEstado;
import com.crm.crm_domus.model.enums.PropiedadTipo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "propiedades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Propiedad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "propietario_cliente_id")
    private Cliente propietario;

    @ManyToOne
    @JoinColumn(name = "propietario_lead_id")
    @jakarta.validation.constraints.NotNull(message = "El lead propietario es obligatorio")
    private Lead leadPropietario;

    @jakarta.validation.constraints.NotNull(message = "El agente asignado es obligatorio")
    @ManyToOne
    @JoinColumn(name = "asignado_agente_id")
    private Usuario asignadoAgente;

    @Enumerated(EnumType.STRING)
    @jakarta.validation.constraints.NotNull(message = "El tipo de operacion es obligatorio")
    private OperacionTipo operacionTipo;

    @Enumerated(EnumType.STRING)
    @jakarta.validation.constraints.NotNull(message = "El estado es obligatorio")
    private PropiedadEstado estado;

    @Enumerated(EnumType.STRING)
    @jakarta.validation.constraints.NotNull(message = "El tipo de propiedad es obligatorio")
    private PropiedadTipo propiedadTipo;

    @jakarta.validation.constraints.NotNull(message = "El precio es obligatorio")
    private Double precio;

    private Integer planta;
    private Integer anioConstruccion;
    private Boolean exclusiva;
    private Double metrosConstruidos;
    private Double metrosUtiles;
    private Integer habitaciones;
    private Integer banios;
    private String direccion;
    private String codigoPostal;
    private String ciudad;
    private String provincia;

    @Column(length = 2000)
    private String descripcion;

    @Column(name = "creado_en", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @ManyToMany
    @JoinTable(
            name = "propiedades_extras",
            joinColumns = @JoinColumn(name = "propiedad_id"),
            inverseJoinColumns = @JoinColumn(name = "extra_id")
    )
    private Set<Extra> extras;

    @OneToMany(mappedBy = "propiedad")
    private Set<Valoracion> valoraciones;
}
