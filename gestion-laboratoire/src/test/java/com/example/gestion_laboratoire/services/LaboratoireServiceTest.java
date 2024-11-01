package com.example.gestion_laboratoire.services;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import com.example.gestion_laboratoire.models.Laboratoire;
import com.example.gestion_laboratoire.test_utils.IBC;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LaboratoireServiceTest {

    private String image1Path = "/home/amidrissi/Pictures/AMI.jpeg";
    private String image2Path = "/home/amidrissi/Pictures/mie-stare.png";

    @Autowired
    private LaboratoireService laboratoireService;

    @LocalServerPort
    // Configuring the testcontainer
    private Integer port;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    // Setting the settings dynamically for the application.properties
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void testLaboratoireActions() throws IOException {

        // Testing the creation of a laboratory
        Laboratoire labo = new Laboratoire("labo_x", "R123456", true, new Date());
        labo.setImageFile(IBC.extractBytes(image1Path));
        ResponseEntity<Object> response = laboratoireService.createLaboratoire(labo);
        assertEquals(response.getStatusCode(), HttpStatus.CREATED);

        // Testing the fetching of all laboratories
        List<Laboratoire> labos = laboratoireService.getLaboratoires();
        assertEquals(1, labos.size());

        // Testing the fetching of a laboratory
        Laboratoire laboFound = laboratoireService.getLaboratoiresById(1L);
        assertNotNull(laboFound, "Labo has been registered");
        assertEquals("labo_x", laboFound.getNom());
        assertEquals("R123456", laboFound.getNrc());
        assertNotNull(laboFound.getLogo(), "Image has been added successfully");

        // Testing the update of a laboratory
        Laboratoire laboBeforeUpdated = new Laboratoire("labo_x69", "R123456789",
        true, new Date());
        labo.setImageFile(IBC.extractBytes(image2Path));
        ResponseEntity<Object> updatedResponse =
        laboratoireService.updateLaboratoire(1L, laboBeforeUpdated);
        assertEquals(updatedResponse.getStatusCode(), HttpStatus.OK);
        Laboratoire laboAfterUpdate = laboratoireService.getLaboratoiresById(1L);
        assertNotNull(laboAfterUpdate, "Labo has been registered");
        assertEquals("labo_x69", laboAfterUpdate.getNom());
        assertEquals("R123456789", laboAfterUpdate.getNrc());
        assertNotEquals(laboBeforeUpdated.getLogo(), laboAfterUpdate.getLogo());

        // Testing the deletion of a laboratory
        ResponseEntity<Object> deleteResponse =
        laboratoireService.deleteLaboratoire(1L);
        assertEquals(deleteResponse.getStatusCode(), HttpStatus.NO_CONTENT);
    }
}
