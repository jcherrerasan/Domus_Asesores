package com.crm.crm_domus.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "extras")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Extra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Extra obligatorio")
    @Column(unique = true, nullable = false)
    private String nombre;

    @ManyToMany(mappedBy = "extras")
    @JsonIgnore
    private Set<Propiedad> propiedades;
}


