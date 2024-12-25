package com.api.gestion_analyse.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.api.gestion_analyse.errors.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import kong.unirest.json.JSONObject;

import org.junit.jupiter.api.*;
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

import com.api.gestion_analyse.DTO.AnalyseDTO;
import com.api.gestion_analyse.DTO.AnalyseDTOExtended;
import com.api.gestion_analyse.models.Analyse;
import com.api.gestion_analyse.repositores.AnalyseRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
public class TestAnalyseService {

    private AnalyseService analyseService;
    @Mock
    private AnalyseRepository analyseRepository;

    @Mock
    private AnalyseExternalCommunicationService analyseExternalCommunicationService;

    @LocalServerPort
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @BeforeEach
    void setup() {
        this.analyseService = new AnalyseService(analyseRepository, analyseExternalCommunicationService);
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
    void testGetAllAnalyses() {
        Analyse analyse = new Analyse(1L, "MRI", new String(new byte[1000]), 2L);
        BDDMockito.when(analyseRepository.findAll()).thenReturn(List.of(analyse));
        List<AnalyseDTO> analysesDTO = analyseService.getAnalyses();
        assertEquals(1, analysesDTO.size());
        assertEquals(new AnalyseDTO(analyse), analysesDTO.get(0));
    }

    @Test
    void testGetAnalyseByValidId() {
        Optional<Analyse> analyse = Optional.of(new Analyse(1L, "MRI", new String(new byte[1000]), 2L));
        JSONObject laboObjectMap = new JSONObject(Map.of("nom", "labo"));
        BDDMockito.when(analyseExternalCommunicationService.getLaboWithId(2L)).thenReturn(laboObjectMap);
        BDDMockito.when(analyseRepository.findById(analyse.get().getId())).thenReturn(analyse);
        AnalyseDTOExtended analyseDTOExtended = analyseService.getAnalyseById(analyse.get().getId());
        assertEquals(new AnalyseDTOExtended(analyse.get(), "labo"), analyseDTOExtended);
    }

    @Test
    void testGetAnalyseByBaaaaadId() {
        BDDMockito.when(analyseRepository.findById(1L)).thenReturn(Optional.empty());
        Throwable t = assertThrows(EntityNotFoundException.class, () -> analyseService.getAnalyseById(1L));
        Assertions.assertEquals("Analyse not found", t.getMessage());
    }

    @Test
    void testCreateValidAnalyse() {
        Analyse analyse = new Analyse(null, "MRI", new String(new byte[1000]), 2L);
        BDDMockito.when(analyseRepository.save(analyse)).thenReturn(analyse);
        ResponseEntity<ApiResponse> response = analyseService.createAnalyse(analyse);
        assertEquals(analyse, (Analyse) response.getBody().getMessage());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testCreateWithNonValidAnalyseRequest() {
        Analyse analyse = new Analyse(null, null, new String(new byte[1000]), null);
        ResponseEntity<ApiResponse> response = analyseService.createAnalyse(analyse);
        assertEquals("Invalid request", response.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testCreateValidAnalyseWithJPAError() {
        Analyse analyse = new Analyse(null, "MRI", new String(new byte[1000]), 2L);
        BDDMockito.when(analyseRepository.save(analyse)).thenThrow(new EntityNotFoundException("rip me"));
        ResponseEntity<ApiResponse> response = analyseService.createAnalyse(analyse);
        assertEquals("There has been an error when creating this analyse", response.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testUpdateAnalyseWithValidId() {
        Analyse analyseNew = new Analyse(null, "xxMRIxx", new String(new byte[100]), 3L);
        Analyse analyse = new Analyse(1L, "MRI", new String(new byte[1000]), 2L);
        BDDMockito.when(analyseRepository.findById(analyse.getId())).thenReturn(Optional.of(analyse));
        ResponseEntity<ApiResponse> response = analyseService.updateAnalyse(analyse.getId(), analyseNew);
        // analyse should be updated
        assertEquals(new AnalyseDTO(analyse), response.getBody().getMessage());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUpdateAnalyseWithNonValidId() {
        Analyse analyseNew = new Analyse(null, "xxMRIxx", new String(new byte[100]), 3L);
        Long id = 1L;
        BDDMockito.when(analyseRepository.findById(id)).thenReturn(Optional.empty());
        ResponseEntity<ApiResponse> response = analyseService.updateAnalyse(id, analyseNew);
        assertEquals("Analyse not found", response.getBody().getMessage());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteAnalyseWithValidId() {
        Optional<Analyse> analyse = Optional.of(new Analyse(1L, "MRI", new String(new byte[1000]), 2L));
        BDDMockito.when(analyseRepository.findById(analyse.get().getId())).thenReturn(analyse);
        ResponseEntity<ApiResponse> response = analyseService.deleteAnalyse(1L);
        assertEquals("Analyse deleted successfully", response.getBody().getMessage());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeleteAnalyseWithNonValidId() {
        Optional<Analyse> analyse = Optional.of(new Analyse(1L, "MRI", new String(new byte[1000]), 2L));
        BDDMockito.when(analyseRepository.findById(analyse.get().getId())).thenReturn(Optional.empty());
        ResponseEntity<ApiResponse> response = analyseService.deleteAnalyse(analyse.get().getId());
        assertEquals("Analyse not found", response.getBody().getMessage());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
