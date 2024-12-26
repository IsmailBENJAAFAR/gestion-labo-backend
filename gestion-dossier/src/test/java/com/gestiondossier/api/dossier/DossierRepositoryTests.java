package com.gestiondossier.api.dossier;

import com.gestiondossier.api.dossier.models.entity.Dossier;
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
class DossierRepositoryTests {

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.0")
            .withReuse(true)
            .withStartupTimeout(Duration.ofSeconds(30));

    @Autowired
    private DossierRepository repository;

    @Test
    void connectionEstabished() {
        assertThat(postgreSQLContainer.isCreated()).isTrue();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
    }

    @BeforeEach
    void setUp() {
        List<Dossier> students = List.of(
                new Dossier(),
                new Dossier(),
                new Dossier(),
                new Dossier()
        );
        repository.saveAll(students);
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void shouldReturnAllDossiers() {
        assertEquals(4, repository.findAll().size());
    }
}
