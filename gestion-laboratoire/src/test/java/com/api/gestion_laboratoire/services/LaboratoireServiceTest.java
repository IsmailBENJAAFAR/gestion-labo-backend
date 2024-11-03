package com.api.gestion_laboratoire.services;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import com.api.gestion_laboratoire.models.Laboratoire;
import com.api.gestion_laboratoire.repositories.LaboratoireRepository;
import jakarta.persistence.EntityNotFoundException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
public class LaboratoireServiceTest {

    private LaboratoireService laboratoireService;
    @Mock
    private CloudinaryService cloudinaryService;
    @Mock
    private LaboratoireRepository laboratoireRepository;

    @LocalServerPort
    // Configuring the testcontainer
    private Integer port;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @BeforeEach
    void setup() {
        this.laboratoireService = new LaboratoireService(laboratoireRepository, cloudinaryService);
    }

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
    void testCreateLaboratoire() throws Exception {
        // Test create action under normal conditions
        Map<String, Object> map = new HashMap<String, Object>() {
            {
                put("url", "some_url");
                put("display_name", "idk");
            }
        };
        Laboratoire laboratoire = new Laboratoire("labo_x", "R123456", true, LocalDate.now());

        BDDMockito.when(cloudinaryService.uploadImage(laboratoire.getImageFile())).thenReturn(map);

        ResponseEntity<Object> response = laboratoireService.createLaboratoire(laboratoire);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    // @Disabled
    void testCreateLaboratoireWithBadImage() throws Exception {
        // Test create action but with a bad image
        Map<String, Object> map = new HashMap<String, Object>() {
            {
                put("error", "some_error");
            }
        };
        Laboratoire laboratoire = new Laboratoire("labo_x", "R123456", true, LocalDate.now());

        BDDMockito.when(cloudinaryService.uploadImage(laboratoire.getImageFile())).thenReturn(map);

        ResponseEntity<Object> response = laboratoireService.createLaboratoire(laboratoire);
        assertEquals(HttpStatus.FAILED_DEPENDENCY, response.getStatusCode());
        assertEquals("Could not create laboratory : " + map.get("error"), response.getBody());
    }

    @Test
    void testCreateLaboratoireWithOfflineCloudinaryService() throws Exception {
        // Test create action if cloudinary services are down
        Laboratoire laboratoire = new Laboratoire("labo_x", "R123456", true, LocalDate.now());

        BDDMockito.when(cloudinaryService.uploadImage(laboratoire.getImageFile())).thenReturn(null);

        ResponseEntity<Object> response = laboratoireService.createLaboratoire(laboratoire);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Unknown error has occured during the creation of the Laboratory", response.getBody());
    }

    @Test
    void testDeleteLaboratoire() {
        // Test delete action under normal conditions
        Optional<Laboratoire> laboratoire = Optional.of(new Laboratoire("labo_x", "R123456", true, LocalDate.now()));

        BDDMockito.given(laboratoireRepository.existsById(1L)).willReturn(true);
        BDDMockito.given(laboratoireRepository.findById(1L)).willReturn(laboratoire);
        BDDMockito.when(cloudinaryService.deleteImage(laboratoire.get().getLogoID()))
                .thenReturn("Image deleted successfully");

        ResponseEntity<Object> response = laboratoireService.deleteLaboratoire(1L);
        verify(laboratoireRepository).deleteById(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals("Laboratory " + laboratoire.get().getNom() + " deleted", response.getBody());
    }

    @Test
    void testDeleteLaboratoireWithBadId() {
        // Test delete action with an invalid id
        BDDMockito.given(laboratoireRepository.existsById(1L)).willReturn(false);

        ResponseEntity<Object> response = laboratoireService.deleteLaboratoire(1L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Laboratory not found", response.getBody());
    }

    @Test
    void testDeleteLaboratoireWithBadCloudinaryCall() {
        // Test delete action if the cloudinary service fails
        Optional<Laboratoire> laboratoire = Optional.of(new Laboratoire("labo_x", "R123456", true, LocalDate.now()));

        BDDMockito.given(laboratoireRepository.existsById(1L)).willReturn(true);
        BDDMockito.given(laboratoireRepository.findById(1L)).willReturn(laboratoire);
        BDDMockito.when(cloudinaryService.deleteImage(laboratoire.get().getLogoID()))
                .thenReturn("something that is not Image deleted successfully");

        ResponseEntity<Object> response = laboratoireService.deleteLaboratoire(1L);
        assertEquals(HttpStatus.FAILED_DEPENDENCY, response.getStatusCode());
    }

    @Test
    void testGetLaboratoires() {
        // Test getAll
        laboratoireService.getLaboratoires();
        verify(laboratoireRepository).findAll();
    }

    @Test
    void testGetLaboratoiresById() {
        // Test get by id action under normal conditions
        Optional<Laboratoire> laboratoire = Optional.of(new Laboratoire("labo_x", "R123456", true, LocalDate.now()));

        BDDMockito.given(laboratoireRepository.existsById(1L)).willReturn(true);
        BDDMockito.given(laboratoireRepository.findById(1L)).willReturn(laboratoire);

        Laboratoire response = laboratoireService.getLaboratoiresById(1L);
        assertNotNull(response);
    }

    @Test
    void testGetLaboratoiresByANonExistingId() {
        // Test get by id action with an invalid id
        BDDMockito.given(laboratoireRepository.existsById(1L)).willReturn(false);
        assertThrows(EntityNotFoundException.class, () -> laboratoireService.getLaboratoiresById(1L));
    }

    @Test
    void testUpdateLaboratoire() {
        // Test update action under normal conditions without an image
        Optional<Laboratoire> laboratoire = Optional.of(new Laboratoire("labo_x", "R123456", true, LocalDate.now()));

        BDDMockito.given(laboratoireRepository.existsById(1L)).willReturn(true);
        BDDMockito.given(laboratoireRepository.findById(1L)).willReturn(laboratoire);

        ResponseEntity<Object> response = laboratoireService.updateLaboratoire(1L,
                new Laboratoire("labo_x69", "R123456789", true, LocalDate.now()));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("labo_x69", laboratoire.get().getNom());
        assertEquals("R123456789", laboratoire.get().getNrc());
        assertEquals("Laboratory " + laboratoire.get().getNom() + " updated", response.getBody());
    }

    @Test
    void testUpdateLaboratoireWithImage() {
        // Test update action under normal conditions with an image this time
        Optional<Laboratoire> laboratoire = Optional.of(new Laboratoire("labo_x", "R123456", true, LocalDate.now()));
        // mimic an incoming a request with an image
        Laboratoire laboUpdate = new Laboratoire("labo_x69", "R123456789", true, LocalDate.now());
        byte[] b = { 1 };
        laboUpdate.setImageFile(b);
        laboUpdate.setLogoID("imageID");

        BDDMockito.given(laboratoireRepository.existsById(1L)).willReturn(true);
        BDDMockito.given(laboratoireRepository.findById(1L)).willReturn(laboratoire);
        BDDMockito.when(cloudinaryService.uploadImage(laboUpdate.getLogoID(), laboUpdate.getImageFile()))
                .thenReturn("url/to/image");

        ResponseEntity<Object> response = laboratoireService.updateLaboratoire(1L, laboUpdate);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("labo_x69", laboratoire.get().getNom());
        assertEquals("R123456789", laboratoire.get().getNrc());
        assertEquals("Laboratory " + laboratoire.get().getNom() + " updated", response.getBody());
        assertEquals("url/to/image", laboratoire.get().getLogo());
    }

    @Test
    void testUpdateLaboratoireWithNonValidId() {
        // Test update action with an invalid id
        BDDMockito.given(laboratoireRepository.existsById(1L)).willReturn(false);

        ResponseEntity<Object> response = laboratoireService.updateLaboratoire(1L,
                new Laboratoire("labo_x69", "R123456789", true, LocalDate.now()));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Laboratory not found", response.getBody());
    }
}
