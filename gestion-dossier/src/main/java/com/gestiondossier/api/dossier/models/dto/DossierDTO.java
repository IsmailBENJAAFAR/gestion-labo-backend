package com.gestiondossier.api.dossier.models.dto;

import com.gestiondossier.api.patient.models.dto.PatientDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DossierDTO {
    private Integer id;
    private String fkEmailUtilisateur;
    private PatientDTO patient;
    private LocalDate date;
}
