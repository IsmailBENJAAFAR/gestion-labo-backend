package com.gestioncontact.api.contactlaboratoire.models.dto;

import com.gestioncontact.api.adresse.models.dto.AdresseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactLaboratoireDTO {
    private Integer id;
    private Integer fkIdLaboratoire;
    private AdresseDTO adresse;
    private String numTel;
    private String fax;
    private String email;
}
