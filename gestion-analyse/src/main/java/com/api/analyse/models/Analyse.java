package com.api.analyse.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.Date;

@Entity
@Data
@Table
@NoArgsConstructor
public class Analyse {
    @Id
    @SequenceGenerator(name = "analyse_sequence", sequenceName = "analyse_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "analyse_sequence")
    private Long id;
    private String nom;
    private String description;
    private Long fkIdLaboratoire;
    @CreationTimestamp
    private Date CreatedAt;
    @UpdateTimestamp
    private Date updatedAt;
}
