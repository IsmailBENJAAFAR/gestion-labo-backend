package com.api.gestioncontact.contact;

import com.api.gestioncontact.adresse.Adresse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Contact {
    @Id
    @GeneratedValue
    private Integer id;
    private int numeroTelophone;
    private int fax;
    private String email;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_adresse")
    private Adresse adresse;

    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
}
