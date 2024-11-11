package com.api.gestionepreuve.epreuve;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Epreuve {
    @Id
    @GeneratedValue
    private int id;
    private String nom;
}
