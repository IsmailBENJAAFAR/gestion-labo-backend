package com.gestioncontact.api.contactlaboratoire;

import com.gestioncontact.api.adresse.models.entity.Adresse;
import com.gestioncontact.api.contactlaboratoire.models.entity.ContactLaboratoire;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ContactLaboratoireRepositoryTests {

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.0")
            .withReuse(true)
            .withStartupTimeout(Duration.ofSeconds(30));

    @Autowired
    private ContactLaboratoireRepository repository;

    @BeforeEach
    void setUp() {
        Adresse adresse1 = Adresse.builder()
                .numVoie("123")
                .nomVoie("Rue de la Science")
                .ville("Paris")
                .codePostal(75001)
                .commune("Paris")
                .build();

        Adresse adresse2 = Adresse.builder()
                .numVoie("456")
                .nomVoie("Avenue de la Recherche")
                .ville("Lyon")
                .codePostal(69002)
                .commune("Lyon")
                .build();

        ContactLaboratoire contact1 = ContactLaboratoire.builder()
                .fkIdLaboratoire(1)
                .adresse(adresse1)
                .numTel("+33 1 23 45 67 89")
                .fax("+33 1 23 45 67 80")
                .email("contact1@laboratoire.fr")
                .build();

        ContactLaboratoire contact2 = ContactLaboratoire.builder()
                .fkIdLaboratoire(2)
                .adresse(adresse2)
                .numTel("+33 4 56 78 90 12")
                .fax("+33 4 56 78 90 13")
                .email("contact2@laboratoire.fr")
                .build();

        repository.saveAll(List.of(contact1, contact2));
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void connectionEstablished() {
        assertThat(postgreSQLContainer.isCreated()).isTrue();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
    }

    @Test
    void shouldReturnAllContactLaboratoires() {
        List<ContactLaboratoire> contacts = repository.findAll();
        assertEquals(2, contacts.size());
    }
}
