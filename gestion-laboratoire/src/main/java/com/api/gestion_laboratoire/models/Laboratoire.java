package com.api.gestion_laboratoire.models;

import java.time.LocalDate;
import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@NoArgsConstructor
public class Laboratoire {
    @Id
    @SequenceGenerator(name = "laboratoire_sequence", sequenceName = "laboratoire_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "laboratoire_sequence")
    private Long id;
    @NotNull
    private String nom;
    private String logo;
    @NotNull
    @Size(max = 9, min = 9, message = "NRC should be exactly 9 characters long")
    private String nrc;
    @NotNull
    private Boolean active = false;
    @NotNull
    private LocalDate dateActivation;
    private String logoID;
    @CreationTimestamp
    private Date CreatedAt;
    @UpdateTimestamp
    private Date updatedAt;
    @Transient
    private byte[] imageFile;

    public Laboratoire(String nom, String nrc, boolean active, LocalDate dateActivation) {
        super();
        this.nom = nom;
        this.nrc = nrc;
        this.active = active;
        this.dateActivation = dateActivation;
    }
}