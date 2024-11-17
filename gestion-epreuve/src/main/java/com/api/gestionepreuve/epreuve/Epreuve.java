package com.api.gestionepreuve.epreuve;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;

@Entity
@Data
public class Epreuve {
    @Id
    @GeneratedValue
    private int id;
    @NotBlank(message = "nom is mandatory")
    private String nom;
    @CreationTimestamp
    private Instant creationAt;
    @UpdateTimestamp
    private Instant modifiedAt;
}
