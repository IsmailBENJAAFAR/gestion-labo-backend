package com.gestioncontact.api.contactlaboratoire.models.dto;

import com.gestioncontact.api.adresse.models.dto.CreateAdresseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateContactLaboratoireDTO {
    private Integer fkIdLaboratoire;
    private CreateAdresseDTO adresse;
    private String numTel;
    private String fax;
    private String email;
}
