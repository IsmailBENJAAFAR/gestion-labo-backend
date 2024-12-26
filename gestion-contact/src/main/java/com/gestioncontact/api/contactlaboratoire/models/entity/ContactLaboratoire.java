package com.gestioncontact.api.contactlaboratoire.models.entity;

import com.gestioncontact.api.adresse.models.entity.Adresse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ContactLaboratoire {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Integer fkIdLaboratoire;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Adresse adresse;

    private String numTel;
    private String fax;
    private String email;
}
