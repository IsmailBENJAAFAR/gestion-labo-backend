package com.api.gestion_analyse.DTO;

import com.api.gestion_analyse.models.Analyse;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class AnalyseDTOExtended extends AnalyseDTO {
    private String laboratoireName;

    public AnalyseDTOExtended(Analyse analyse, String laboratoireName) {
        super(analyse);
        this.laboratoireName = laboratoireName;
    }
}
