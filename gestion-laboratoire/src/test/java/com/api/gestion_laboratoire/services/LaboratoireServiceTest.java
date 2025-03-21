package com.api.gestion_laboratoire.services;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.naming.CommunicationException;

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

import com.api.gestion_laboratoire.dto.LaboratoireDTO;
import com.api.gestion_laboratoire.errors.ApiResponse;
import com.api.gestion_laboratoire.models.Laboratoire;
import com.api.gestion_laboratoire.repositories.LaboratoireRepository;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.persistence.EntityNotFoundException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class LaboratoireServiceTest {

    private LaboratoireService laboratoireService;
    @Mock
    private StorageService storageService;
    @Mock
    private LaboratoireRepository laboratoireRepository;
    @Mock
    private LaboratoireEventsService laboratoireEventsService;

    @LocalServerPort
    // Configuring the testcontainer

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @BeforeEach
    void setup() {
        this.laboratoireService = new LaboratoireService(laboratoireRepository, storageService,
                laboratoireEventsService);
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
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
        registry.add("dependency.count", () -> 2);
    }

    @Test
    void testCreateLaboratoire() {
        // Test create action under normal conditions
        Map<String, Object> map = new HashMap<>() {
            {
                put("url", "some_url");
                put("display_name", "idk");
            }
        };
        // with a good request
        Laboratoire laboratoire = new Laboratoire("labo_x", "123456789", true, LocalDate.now());
        // with a bad request (invalid NRC)
        Laboratoire invalidLaboratoire = new Laboratoire("labo_x", "56789", true, LocalDate.now());

        BDDMockito.when(storageService.uploadImage(laboratoire.getImageFile())).thenReturn(map);

        BDDMockito.when(laboratoireRepository.save(laboratoire)).thenReturn(laboratoire);

        ResponseEntity<ApiResponse> response = laboratoireService.createLaboratoire(laboratoire);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(
                new LaboratoireDTO(null, "labo_x", "some_url", "123456789", true, LocalDate.now(), "idk"),
                (LaboratoireDTO) response.getBody().getMessage());

        ResponseEntity<ApiResponse> responseWithInvalidNrc = laboratoireService.createLaboratoire(invalidLaboratoire);
        assertEquals(HttpStatus.BAD_REQUEST, responseWithInvalidNrc.getStatusCode());
    }

    @Test
    void testCreateLaboratoireWithBadImage() {
        // Test create action but with a bad image
        Map<String, Object> map = new HashMap<>() {
            {
                put("error", "some_error");
            }
        };
        Laboratoire laboratoire = new Laboratoire("labo_x", "123456789", true,
                LocalDate.now());

        BDDMockito.when(storageService.uploadImage(laboratoire.getImageFile())).thenReturn(map);

        ResponseEntity<ApiResponse> response = laboratoireService.createLaboratoire(laboratoire);
        assertEquals(HttpStatus.FAILED_DEPENDENCY, response.getStatusCode());
        assertEquals("La creation du laboratoire a échoué : " + map.get("error"),
                response.getBody().getMessage());
    }

    @Test
    void testCreateLaboratoireWithOfflineStorageService() {
        // Test create action if cloudinary services are down
        Laboratoire laboratoire = new Laboratoire("labo_x", "123456789", true,
                LocalDate.now());

        BDDMockito.when(storageService.uploadImage(laboratoire.getImageFile())).thenReturn(null);

        ResponseEntity<ApiResponse> response = laboratoireService.createLaboratoire(laboratoire);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Une erreur s'est produite lors de la création du laboratoire",
                response.getBody().getMessage());
    }

    @Test
    void testDeleteLaboratoire() throws CommunicationException, JsonProcessingException {
        // Test delete action under normal conditions
        Optional<Laboratoire> laboratoire = Optional.of(new Laboratoire("labo_x",
                "123456789", true, LocalDate.now()));

        BDDMockito.when(laboratoireRepository.findById(1L)).thenReturn(laboratoire);
        BDDMockito.when(laboratoireEventsService.canDeleteLaboratoire(1L)).thenReturn(true);
        BDDMockito.when(storageService.deleteImage(laboratoire.get().getLogoID()))
                .thenReturn("Image supprimée avec succèsy");

        ResponseEntity<ApiResponse> response = laboratoireService.deleteLaboratoire(1L);
        verify(laboratoireRepository).deleteById(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals("Laboratoire supprimé avec succès", response.getBody().getMessage());
    }

    @Test
    void testDeleteLaboratoireButHasDependencies() throws CommunicationException, JsonProcessingException {
        Optional<Laboratoire> laboratoire = Optional.of(new Laboratoire("labo_x",
                "123456789", true, LocalDate.now()));

        BDDMockito.when(laboratoireRepository.findById(1L)).thenReturn(laboratoire);
        BDDMockito.when(laboratoireEventsService.canDeleteLaboratoire(1L)).thenReturn(false);

        ResponseEntity<ApiResponse> response = laboratoireService.deleteLaboratoire(1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Le laboratoire a des dépendances", response.getBody().getMessage());
    }

    @Test
    void testDeleteLaboratoireWhenNull() throws JsonProcessingException {
        Optional<Laboratoire> laboratoire = Optional.of(new Laboratoire("labo_x",
                "123456789", true, LocalDate.now()));

        BDDMockito.when(laboratoireRepository.findById(1L)).thenReturn(laboratoire);
        BDDMockito.when(laboratoireEventsService.canDeleteLaboratoire(1L)).thenReturn(null);

        assertThrows(CommunicationException.class, () -> laboratoireService.deleteLaboratoire(1L));
    }

    @Test
    void testDeleteLaboratoireWithBadId() throws CommunicationException, JsonProcessingException {
        // Test delete action with an invalid id
        ResponseEntity<ApiResponse> response = laboratoireService.deleteLaboratoire(1L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Laboratoire introuvable", response.getBody().getMessage());
    }

    @Test
    void testDeleteLaboratoireWithBadCloudinaryCall() throws CommunicationException, JsonProcessingException {
        // Test delete action if the cloudinary service fails
        Optional<Laboratoire> laboratoire = Optional.of(new Laboratoire("labo_x",
                "123456789", true, LocalDate.now()));

        BDDMockito.when(laboratoireRepository.findById(1L)).thenReturn(laboratoire);
        BDDMockito.when(laboratoireEventsService.canDeleteLaboratoire(1L)).thenReturn(true);

        BDDMockito.when(storageService.deleteImage(laboratoire.get().getLogoID()))
                .thenReturn("Echec de la suppression de l'image");
        ResponseEntity<ApiResponse> response = laboratoireService.deleteLaboratoire(1L);
        assertEquals(HttpStatus.FAILED_DEPENDENCY, response.getStatusCode());

        BDDMockito.when(storageService.deleteImage(laboratoire.get().getLogoID()))
                .thenReturn("Impossible de supprimer l'image : Image introuvable");
        ResponseEntity<ApiResponse> response2 = laboratoireService.deleteLaboratoire(1L);
        assertEquals(HttpStatus.NO_CONTENT, response2.getStatusCode());
    }

    @Test
    void testGetLaboratoires() {
        // Test getAll
        List<Laboratoire> labos = List.of(new Laboratoire("labo_x", "123456789",
                true, LocalDate.now()));
        BDDMockito.when(laboratoireRepository.findAll()).thenReturn(labos);
        assertEquals(laboratoireService.getLaboratoires().get(0), new LaboratoireDTO(labos.get(0)));
    }

    @Test
    void testGetLaboratoiresById() throws EntityNotFoundException {
        // Test get by id action under normal conditions
        Optional<Laboratoire> laboratoire = Optional.of(new Laboratoire("labo_x",
                "123456789", true, LocalDate.now()));

        BDDMockito.when(laboratoireRepository.findById(1L)).thenReturn(laboratoire);

        LaboratoireDTO response = laboratoireService.getLaboratoiresById(1L);
        assertNotNull(response);
    }

    @Test
    void testGetLaboratoiresByANonExistingId() {
        // Test get by id action with an invalid id
        assertThrows(EntityNotFoundException.class, () -> laboratoireService.getLaboratoiresById(1L));
    }

    @Test
    void testUpdateLaboratoire() {
        Laboratoire laboratoire = new Laboratoire("labo_x", "123456789", true,
                LocalDate.now());
        BDDMockito.when(laboratoireRepository.findById(1L)).thenReturn(Optional.of(laboratoire));

        // Update with a valid request
        Laboratoire newLaboratoire = new Laboratoire("labo_x69", "999999999", false,
                LocalDate.of(2011, 11, 11));
        ResponseEntity<ApiResponse> response = laboratoireService.updateLaboratoire(1L, newLaboratoire);
        assertEquals(new LaboratoireDTO(laboratoire),
                response.getBody().getMessage());
        assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    void testUpdateWithBadNRC() {
        // Update with bad NRC in request
        ResponseEntity<ApiResponse> responseWithInvalidNrc = laboratoireService.updateLaboratoire(1L,
                new Laboratoire("labo_x69", "99999999966666", true, LocalDate.now()));
        assertEquals(HttpStatus.BAD_REQUEST, responseWithInvalidNrc.getStatusCode());
    }

    @Test
    void testUpdateLaboratoireWithImage() {
        // Test update action under normal conditions with an image this time
        Laboratoire laboratoire = new Laboratoire("labo_x", "999999999", true,
                LocalDate.now());
        laboratoire.setId(1L);
        // mimic an incoming a request with an image
        Laboratoire laboUpdate = new Laboratoire("labo_x69", "123456789", true,
                LocalDate.now());
        byte[] b = { 1 };
        laboUpdate.setImageFile(b);
        laboUpdate.setLogoID("imageID");

        BDDMockito.when(laboratoireRepository.findById(1L)).thenReturn(Optional.of(laboratoire));
        BDDMockito.when(storageService.uploadImage(laboUpdate.getLogoID(),
                laboUpdate.getImageFile()))
                .thenReturn("url/to/image");

        ResponseEntity<ApiResponse> response = laboratoireService.updateLaboratoire(1L, laboUpdate);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new LaboratoireDTO(laboratoire),
                response.getBody().getMessage());
    }

    @Test
    void testUpdateLaboratoireWithNonValidId() {
        // Test update action with an invalid id

        ResponseEntity<ApiResponse> response = laboratoireService.updateLaboratoire(1L,
                new Laboratoire("labo_x69", "123456789", true, LocalDate.now()));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Laboratoire introuvable", response.getBody().getMessage());
    }
}
