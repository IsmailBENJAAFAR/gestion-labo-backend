package com.api.analyse.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Data
@Table
@NoArgsConstructor
public class Analyse {
    @Id
    @SequenceGenerator(name = "analyse_sequence", sequenceName = "analyse_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "analyse_sequence")
    @NotEmpty
    private Long id;
    @NotEmpty
    private String nom;
    private String description;
    @NotEmpty
    private Long fkIdLaboratoire;
    @CreationTimestamp
    private Date CreatedAt;
    @UpdateTimestamp
    private Date updatedAt;
}
