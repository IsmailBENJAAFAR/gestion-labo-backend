package com.gestioncontact.api.adresse;

import com.gestioncontact.api.adresse.models.entity.Adresse;
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
class AdresseRepositoryTests {

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.0")
            .withReuse(true)
            .withStartupTimeout(Duration.ofSeconds(30));

    @Autowired
    private AdresseRepository repository;

    @Test
    void connectionEstabished() {
        assertThat(postgreSQLContainer.isCreated()).isTrue();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
    }

    @BeforeEach
    void setUp() {
        List<Adresse> adresses = List.of(
                new Adresse(null, "10", "Rue de la Paix", 75001, "Rabat", "Rabat"),
                new Adresse(null, "22", "Avenue ibn tachfin", 75008, "Marakech", "Marakech"),
                new Adresse(null, "15", "Boulevard Khattabi", 75006, "Tanger", "Tanger"),
                new Adresse(null, "5", "Rue de Fès", 75004, "Oujda", "Oujda")
        );
        repository.saveAll(adresses);
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void shouldReturnAllAdresses() {
        assertEquals(4, repository.findAll().size());
    }
}
