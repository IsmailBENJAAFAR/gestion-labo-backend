package com.example.gestion_laboratoire.Models;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Laboratoire {
    @Id
    @SequenceGenerator(name = "laboratoire_sequence", sequenceName = "laboratoire_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "laboratoire_sequence")
    private Long id;
    private String nom;
    private String logo;
    private String nrc;
    private Boolean active = false;
    private Date dateActivation;
    @CreationTimestamp
    private Date CreatedAt;
    @UpdateTimestamp
    private Date updatedAt;
    @Transient
    private byte[] imageFile;

    public Laboratoire(String nom, String logo, String nrc, boolean active, Date dateActivation) {
        super();
        this.nom = nom;
        this.logo = logo;
        this.nrc = nrc;
        this.active = active;
        this.dateActivation = dateActivation;
    }
}