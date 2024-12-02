package com.api.gestion_laboratoire.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;

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
import com.api.gestion_laboratoire.services.LaboratoireService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class LaboratoireControllerTest {

    private LaboratoireController laboratoireController;
    @Mock
    private LaboratoireService laboratoireService;
    @Mock
    private LaboratoireRepository laboratoireRepository;

    @LocalServerPort
    // Configuring the testcontainer

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @BeforeEach
    void setup() {
        this.laboratoireController = new LaboratoireController(laboratoireService);
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
    }

    @Test
    void testGetAll() {
        Laboratoire laboratoire = new Laboratoire("labo_x", "123456789", true, LocalDate.now());
        laboratoire.setId(1L);
        List<LaboratoireDTO> laboList = List.of(new LaboratoireDTO(laboratoire));

        BDDMockito.when(laboratoireService.getLaboratoires()).thenReturn(laboList);

        assertEquals(laboList.get(0), laboratoireController.getAll().get(0));
    }

    @Test
    void testGetById() {
        Laboratoire laboratoire = new Laboratoire("labo_x", "123456789", true, LocalDate.now());
        long laboId = 1L;
        laboratoire.setId(laboId);
        LaboratoireDTO laboDTO = new LaboratoireDTO(laboratoire);

        BDDMockito.when(laboratoireService.getLaboratoiresById(laboId)).thenReturn(laboDTO);

        assertEquals(laboDTO, laboratoireController.getById(laboId));
    }

    @Test
    void testCreateLaboratoire() {
        long laboId = 1L;
        Laboratoire laboratoire = new Laboratoire("labo_x", "123456789", true, LocalDate.now());
        laboratoire.setId(laboId);
        ResponseEntity<ApiResponse> response = new ResponseEntity<>(new ApiResponse("creation success"),
                HttpStatus.CREATED);

        BDDMockito.when(laboratoireService.createLaboratoire(laboratoire)).thenReturn(response);

        assertEquals(response.getStatusCode(), laboratoireController.create(laboratoire).getStatusCode());
        assertEquals(response.getBody().getMessage(), laboratoireController.create(laboratoire).getBody().getMessage());
    }

    @Test
    void testUpdateLaboratoire() {
        long laboId = 1L;
        Laboratoire laboratoire = new Laboratoire("labo_y", "123456000", true, LocalDate.now());
        laboratoire.setId(laboId);
        ResponseEntity<ApiResponse> response = new ResponseEntity<>(new ApiResponse("update success"),
                HttpStatus.OK);

        BDDMockito.when(laboratoireService.updateLaboratoire(laboId, laboratoire)).thenReturn(response);

        assertEquals(response.getStatusCode(), laboratoireController.update(laboId, laboratoire).getStatusCode());
        assertEquals(response.getBody().getMessage(),
                laboratoireController.update(laboId, laboratoire).getBody().getMessage());
    }

    @Test
    void testDeleteLaboratoire() {
        long laboId = 1L;
        ResponseEntity<ApiResponse> response = new ResponseEntity<>(new ApiResponse("deletion success"),
                HttpStatus.OK);

        BDDMockito.when(laboratoireService.deleteLaboratoire(laboId)).thenReturn(response);

        assertEquals(response.getStatusCode(), laboratoireController.delete(laboId).getStatusCode());
        assertEquals(response.getBody().getMessage(),
                laboratoireController.delete(laboId).getBody().getMessage());
    }

}
