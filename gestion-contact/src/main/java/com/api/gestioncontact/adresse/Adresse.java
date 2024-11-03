package com.api.gestioncontact.adresse;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Adresse {
    
    @Id
    @GeneratedValue
    private Integer id;
    private int numVoie;
    private String nomVoie;
    private int codePostale;
    private String ville;
    private String commune;
}
