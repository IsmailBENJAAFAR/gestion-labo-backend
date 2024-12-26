package com.gestioncontact.api.adresse.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAdresseDTO {
    private String numVoie;
    private String nomVoie;
    private int codePostal;
    private String ville;
    private String commune;
}
