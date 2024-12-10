package com.api.analyse.DTO;

import com.api.analyse.models.Analyse;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AnalyseDTO {
    private Long id;
    private String nom;
    private String description;
    private Long fkIdLaboratoire;

    public AnalyseDTO(Analyse analyse) {
        this.id = analyse.getId();
        this.nom = analyse.getNom();
        this.description = analyse.getDescription();
        this.fkIdLaboratoire = analyse.getFkIdLaboratoire();
    }
}
