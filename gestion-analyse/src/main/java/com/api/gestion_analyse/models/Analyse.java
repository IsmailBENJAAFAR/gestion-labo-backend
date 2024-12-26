package com.api.gestion_analyse.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Table(name = "table_analyse")
@Data
@NoArgsConstructor
public class Analyse {
    @Id
    @SequenceGenerator(name = "analyse_sequence", sequenceName = "analyse_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "analyse_sequence")
    private Long id;
    @NotBlank
    private String nom;
    @Column(columnDefinition = "TEXT")
    private String description;
    @NotNull
    private Long fkIdLaboratoire;
    @CreationTimestamp
    private Date createdAt;
    @UpdateTimestamp
    private Date updatedAt;

    public Analyse(Long id, String nom, String description, Long fkIdLaboratoire) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.fkIdLaboratoire = fkIdLaboratoire;
    }

    public Analyse(String nom, String description, Long fkIdLaboratoire) {
        this.nom = nom;
        this.description = description;
        this.fkIdLaboratoire = fkIdLaboratoire;
    }

}
