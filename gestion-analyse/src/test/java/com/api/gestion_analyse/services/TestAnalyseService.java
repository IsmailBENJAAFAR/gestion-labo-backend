package com.api.gestion_analyse.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Optional;

import com.api.gestion_analyse.errors.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.persistence.EntityNotFoundException;

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
import com.api.gestion_analyse.models.Analyse;
import com.api.gestion_analyse.repositores.AnalyseRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class TestAnalyseService {

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
        BDDMockito.when(analyseRepository.findById(analyse.get().getId())).thenReturn(analyse);
        AnalyseDTO analyseDTO = analyseService.getAnalyseById(analyse.get().getId());
        assertEquals(new AnalyseDTO(analyse.get()), analyseDTO);
    }

    @Test
    void testGetAnalyseByBaaaaadId() {
        Long id = 1L;
        BDDMockito.when(analyseRepository.findById(id)).thenReturn(Optional.empty());
        Throwable t = assertThrows(EntityNotFoundException.class, () -> analyseService.getAnalyseById(id));
        Assertions.assertEquals("Analyse introuvable", t.getMessage());
    }

    @Test
    void testCreateValidAnalyse() throws JsonProcessingException {
        Analyse analyse = new Analyse(null, "MRI", new String(new byte[1000]), 2L);
        BDDMockito.when(analyseExternalCommunicationService.checkIfLaboratoireExists(analyse.getFkIdLaboratoire()))
                .thenReturn(true);
        BDDMockito.when(analyseRepository.save(analyse)).thenReturn(analyse);
        ResponseEntity<ApiResponse> response = analyseService.createAnalyse(analyse);
        assertEquals(new AnalyseDTO(analyse), (AnalyseDTO) response.getBody().getMessage());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testCreateWithNonValidAnalyseRequest() throws JsonProcessingException {
        Analyse analyse = new Analyse(null, null, new String(new byte[1000]), null);
        ResponseEntity<ApiResponse> response = analyseService.createAnalyse(analyse);
        assertEquals("Requete invalide", response.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testCreateWithNonValidLaboratoireFkId() throws JsonProcessingException {
        Analyse analyse = new Analyse(1L, "xxxx", new String(new byte[1000]), 999L);
        BDDMockito.when(analyseExternalCommunicationService.checkIfLaboratoireExists(analyse.getFkIdLaboratoire()))
                .thenReturn(false);
        ResponseEntity<ApiResponse> response = analyseService.createAnalyse(analyse);
        assertEquals("Identifiant de laboratoire invalide", response.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testCreateWithTimedOutLaboratoireFkIdMessage() throws JsonProcessingException {
        Analyse analyse = new Analyse(1L, "xxxx", new String(new byte[1000]), 999L);
        BDDMockito.when(analyseExternalCommunicationService.checkIfLaboratoireExists(analyse.getFkIdLaboratoire()))
                .thenReturn(null);
        ResponseEntity<ApiResponse> response = analyseService.createAnalyse(analyse);
        assertEquals("Communication échouée avec l'un des services", response.getBody().getMessage());
        assertEquals(HttpStatus.REQUEST_TIMEOUT, response.getStatusCode());
    }

    @Test
    void testCreateValidAnalyseWithJPAError() throws JsonProcessingException {
        Analyse analyse = new Analyse(null, "MRI", new String(new byte[1000]), 2L);
        BDDMockito.when(analyseExternalCommunicationService.checkIfLaboratoireExists(analyse.getFkIdLaboratoire()))
                .thenReturn(true);
        BDDMockito.when(analyseRepository.save(analyse)).thenThrow(new EntityNotFoundException("rip me"));
        ResponseEntity<ApiResponse> response = analyseService.createAnalyse(analyse);
        assertEquals("Une erreur s'est produite lors de la création de l'analyse", response.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testUpdateAnalyseWithValidId() throws JsonProcessingException {
        Analyse analyseNew = new Analyse(null, "xxMRIxx", new String(new byte[100]), 3L);
        Analyse analyseOld = new Analyse(1L, "MRI", new String(new byte[1000]), 2L);
        BDDMockito.when(analyseExternalCommunicationService.checkIfLaboratoireExists(analyseNew.getFkIdLaboratoire()))
                .thenReturn(true);
        BDDMockito.when(analyseRepository.findById(analyseOld.getId())).thenReturn(Optional.of(analyseOld));
        ResponseEntity<ApiResponse> response = analyseService.updateAnalyse(analyseOld.getId(), analyseNew);
        // analyseOld should be updated
        assertEquals(new AnalyseDTO(analyseOld), response.getBody().getMessage());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testUpdateAnalyseWithNonValidId() throws JsonProcessingException {
        Analyse analyseNew = new Analyse(null, "xxMRIxx", new String(new byte[100]), 3L);
        Long id = 1L;
        BDDMockito.when(analyseExternalCommunicationService.checkIfLaboratoireExists(analyseNew.getFkIdLaboratoire()))
                .thenReturn(true);
        BDDMockito.when(analyseRepository.findById(id)).thenReturn(Optional.empty());
        ResponseEntity<ApiResponse> response = analyseService.updateAnalyse(id, analyseNew);
        assertEquals("Analyse introuvable", response.getBody().getMessage());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testUpdateAnalyseWithNonValidLaboratoireFkId() throws JsonProcessingException {
        Analyse analyseNew = new Analyse(null, "xxMRIxx", new String(new byte[100]), 3L);
        Long id = 1L;
        BDDMockito.when(analyseExternalCommunicationService.checkIfLaboratoireExists(analyseNew.getFkIdLaboratoire()))
                .thenReturn(false);
        ResponseEntity<ApiResponse> response = analyseService.updateAnalyse(id, analyseNew);
        assertEquals("Identifiant de laboratoire invalide", response.getBody().getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testUpdateAnalyseWithTimedOutLaboratoireFkIdMessage() throws JsonProcessingException {
        Analyse analyseNew = new Analyse(null, "xxMRIxx", new String(new byte[100]), 3L);
        Long id = 1L;
        BDDMockito.when(analyseExternalCommunicationService.checkIfLaboratoireExists(analyseNew.getFkIdLaboratoire()))
                .thenReturn(null);
        ResponseEntity<ApiResponse> response = analyseService.updateAnalyse(id, analyseNew);
        assertEquals("Communication échouée avec l'un des services", response.getBody().getMessage());
        assertEquals(HttpStatus.REQUEST_TIMEOUT, response.getStatusCode());
    }

    @Test
    void testDeleteAnalyseWithValidId() {
        Optional<Analyse> analyse = Optional.of(new Analyse(1L, "MRI", new String(new byte[1000]), 2L));
        BDDMockito.when(analyseRepository.findById(analyse.get().getId())).thenReturn(analyse);
        ResponseEntity<ApiResponse> response = analyseService.deleteAnalyse(1L);
        assertEquals("Analyse supprimée avec succès", response.getBody().getMessage());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testDeleteAnalyseWithNonValidId() {
        Optional<Analyse> analyse = Optional.of(new Analyse(1L, "MRI", new String(new byte[1000]), 2L));
        BDDMockito.when(analyseRepository.findById(analyse.get().getId())).thenReturn(Optional.empty());
        ResponseEntity<ApiResponse> response = analyseService.deleteAnalyse(analyse.get().getId());
        assertEquals("Analyse introuvable", response.getBody().getMessage());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
