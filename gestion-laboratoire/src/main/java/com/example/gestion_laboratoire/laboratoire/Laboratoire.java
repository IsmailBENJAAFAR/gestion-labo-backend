package com.example.gestion_laboratoire.laboratoire;

import java.util.Date;

public class Laboratoire {
    private long id;
    private String nom;
    private String logo;
    private String nrc;
    private boolean active;
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
