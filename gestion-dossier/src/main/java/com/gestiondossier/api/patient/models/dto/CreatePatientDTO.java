package com.gestiondossier.api.patient.models.dto;

import com.gestiondossier.api.patient.models.entity.Sexe;
import com.gestiondossier.api.patient.models.entity.TypePieceIdentite;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePatientDTO {
    private String nomComplet;
    private LocalDate dateNaissance;
    private Sexe sexe;
    private TypePieceIdentite typePieceIdentite;
    private String numPieceIdentite;
    private String adresse;
    private String numTel;
    private String email;
}
