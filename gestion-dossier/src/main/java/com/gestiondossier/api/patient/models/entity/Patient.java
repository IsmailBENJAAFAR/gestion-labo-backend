package com.gestiondossier.api.patient.models.entity;

import com.gestiondossier.api.dossier.models.entity.Dossier;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String nomComplet;
    private LocalDate dateNaissance;
    @Enumerated(EnumType.STRING)
    private Sexe sexe;
    @Enumerated(EnumType.STRING)
    private TypePieceIdentite typePieceIdentite;
    private String numPieceIdentite;
    private String adresse;
    private int numTel;
    private String email;

    @OneToOne(mappedBy = "patient")
    private Dossier dossier;
}
