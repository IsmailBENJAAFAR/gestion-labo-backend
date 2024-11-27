package com.api.gestion_laboratoire.dto;

import java.time.LocalDate;

import com.api.gestion_laboratoire.models.Laboratoire;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LaboratoireDTO {
    private Long id;
    @NotEmpty
    private String nom;
    @NotEmpty
    private String logo;
    @NotEmpty
    @Size(max = 9, min = 9, message = "NRC should be exactly 9 characters long")
    private String nrc;
    @NotNull
    private Boolean active = false;
    @NotNull
    private LocalDate dateActivation;
    @NotEmpty
    private String logoID;

    public LaboratoireDTO(Laboratoire labo) {
        this.id = labo.getId();
        this.nom = labo.getNom();
        this.logo = labo.getLogo();
        this.nrc = labo.getNrc();
        this.active = labo.getActive();
        this.dateActivation = labo.getDateActivation();
        this.logoID = labo.getLogoID();
    }
}
