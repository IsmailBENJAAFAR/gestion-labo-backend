package com.example.gestion_laboratoire.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import com.example.gestion_laboratoire.models.Laboratoire;
import com.example.gestion_laboratoire.test_utils.IBC;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ServicesTest {

    @Autowired
    private LaboratoireService laboratoireService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @LocalServerPort
    // Configuring the testcontainer
    private Integer port;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @BeforeAll
    static void startBeforeAll() {
        postgres.start();
    }

    @AfterAll
    static void stopAfterAll() {
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
    void testCreateLaboratoire() throws IOException {

        System.out.println("Testing image upload");
        Map<String, Object> imageInfo = cloudinaryService
                .uploadImage(IBC.extractBytes("/home/amidrissi/Pictures/AMI.jpeg"));
        assertNotNull(imageInfo, "the image actually saved");

        System.out.println("Testing image deletion");
        assertEquals("Image deleted successfully",
                cloudinaryService.deleteImage(String.valueOf(imageInfo.get("display_name"))));

        System.out.println("Testing the creation of a laboratory");
        Laboratoire labo = new Laboratoire("labo_x", "R123456", true, new Date());
        labo.setImageFile(IBC.extractBytes("/home/amidrissi/Pictures/AMI.jpeg"));
        ResponseEntity<Object> response = laboratoireService.createLaboratoire(labo);
        assertEquals(response.getStatusCode(), HttpStatus.CREATED);

        System.out.println("Testing the fetching of all laboratories");
        List<Laboratoire> labos = laboratoireService.getLaboratoires();
        assertEquals(1, labos.size());

        System.out.println("Testing the fetching of a laboratory");
        Laboratoire laboFound = laboratoireService.getLaboratoiresById(1L);
        assertNotNull(laboFound, "Labo has been registered");
        assertEquals("labo_x", laboFound.getNom());
        assertEquals("R123456", laboFound.getNrc());
        assertNotNull(laboFound.getLogo(), "Image has been added successfully");

        System.out.println("Testing the update of a laboratory");
        Laboratoire laboUpdated = new Laboratoire("labo_x69", "R123456789", true, new Date());
        labo.setImageFile(IBC.extractBytes("/home/amidrissi/Pictures/mie-stare.png"));
        ResponseEntity<Object> updatedResponse = laboratoireService.updateLaboratoire(1L, laboUpdated);
        assertEquals(updatedResponse.getStatusCode(), HttpStatus.OK);
        Laboratoire laboAfterUpdate = laboratoireService.getLaboratoiresById(1L);
        assertNotNull(laboAfterUpdate, "Labo has been registered");
        assertEquals("labo_x69", laboAfterUpdate.getNom());
        assertEquals("R123456789", laboAfterUpdate.getNrc());

        System.out.println("Testing the deletion of a laboratory");
        ResponseEntity<Object> deleteResponse = laboratoireService.deleteLaboratoire(1L);
        assertEquals(deleteResponse.getStatusCode(), HttpStatus.NO_CONTENT);
    }
}
