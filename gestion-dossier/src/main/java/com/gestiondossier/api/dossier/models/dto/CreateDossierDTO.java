package com.gestiondossier.api.dossier.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateDossierDTO {
    private String fkEmailUtilisateur;
    private Integer patientId;
    private LocalDate date;
}
