package com.example.gestion_laboratoire.laboratoire;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table
public class Laboratoire {
    @Id
    @SequenceGenerator(name = "laboratoire_sequence", sequenceName = "laboratoire_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "laboratoire_sequence")
    private Long id;
    private String nom;
    private String logo;
    private String nrc;
    @Column(columnDefinition = "boolean default false")
    private Boolean active = false;
    private Date dateActivation; // TODO: either Date or LocalDate, gotta decide

    public Laboratoire() {
        super();
    }

    public Laboratoire(String nom, String logo, String nrc, boolean active, Date dateActivation) {
        super();
        this.nom = nom;
        this.logo = logo;
        this.nrc = nrc;
        this.active = active;
        this.dateActivation = dateActivation;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getLogo() {
        return this.logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getNrc() {
        return this.nrc;
    }

    public void setNrc(String nrc) {
        this.nrc = nrc;
    }

    public boolean isActive() {
        return this.active;
    }

    public boolean getActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getDateActivation() {
        return this.dateActivation;
    }

    public void setDateActivation(Date dateActivation) {
        this.dateActivation = dateActivation;
    }

}
